package com.example.foodorderingapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.R
import com.example.foodorderingapp.RiderApp.RiderHomeActivity
import com.example.foodorderingapp.Utils.Constants.ERROR
import com.example.foodorderingapp.Utils.Constants.INFORMATION
import com.example.foodorderingapp.Utils.Constants.ROLE_RIDER
import com.example.foodorderingapp.Utils.Constants.ROLE_USER
import com.example.foodorderingapp.Utils.Helper.isValidEmail
import com.example.foodorderingapp.Utils.Helper.showAlertDialog
import com.example.foodorderingapp.Utils.Helper.showError
import com.example.foodorderingapp.Utils.Helper.togglePasswordVisibility
import com.example.foodorderingapp.databinding.ActivityLoginBinding
import com.example.foodorderingapp.viewModels.LoginViewModel
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject
import java.lang.ref.WeakReference

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setupGoogleSignInClient()
        setupLoginListeners()
    }

    private fun setupGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupLoginListeners() {

        with(binding) {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                when {
                    email.isBlank() -> {
                        etEmail.showError(getString(R.string.email_required_error))
                    }
                    !email.isValidEmail() -> {
                        etEmail.showError(getString(R.string.email_valid_error))
                    }
                    password.isBlank() -> {
                        etPassword.showError(getString(R.string.password_required_error))
                    }
                    else -> {
                        binding.apply {
                            svLogin.alpha = 0.5f
                            progressBarLogin.visibility = View.VISIBLE
                            btnLogin.isEnabled = false
                        }
                        loginViewModel.firebaseAuthWithEmailPass(
                            email,
                            password,
                            object : CustomSuccessFailureListener {
                                override fun onSuccess() {
                                    auth.currentUser?.apply {
//                                        if (isEmailVerified) {
                                            uid?.let { uid ->
                                                loginViewModel.checkRole(uid) { role ->
                                                    navigateBasedOnRole(role)
                                                }
                                            }
//                                        } else {
//                                            sendEmailVerification()
//                                            showAlertDialog(
//                                                WeakReference(this@LoginActivity),
//                                                INFORMATION, "Please verify your email"
//                                            )
//
//                                        }
                                    }
                                }

                                override fun onFailure(errorMessage: String?) {
                                    showAlertDialog(
                                        WeakReference(this@LoginActivity),
                                        ERROR,
                                        errorMessage
                                    )
                                    binding.apply {
                                        svLogin.alpha = 1f
                                        progressBarLogin.visibility = View.GONE
                                        btnLogin.isEnabled = true
                                    }
                                    binding.progressBarLogin.visibility = View.GONE
                                }

                            })
                    }
                }
            }

            btnGoogleLogin.setOnClickListener {
                signInGoogle()
            }

            ivEye.setOnClickListener {
                etPassword.togglePasswordVisibility()
            }

            tvSignup.setOnClickListener {
                startActivity(
                    Intent(this@LoginActivity, SignUpActivity::class.java)
                )
            }
        }


    }

    private fun navigateBasedOnRole(role: String?) {
        val destinationClass = when (role) {
            ROLE_RIDER -> {
                RiderHomeActivity::class.java
            }
            ROLE_USER -> {
                MainActivity::class.java
            }
            else -> {
                MainActivity::class.java
            }
        }
        loginViewModel.saveRoleInPref(role)
        startActivity(Intent(this@LoginActivity, destinationClass))
        finish()
    }


    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            account?.let {
                val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
                loginViewModel.firebaseAuthWithCredentials(credentials,
                    object : CustomSuccessFailureListener {
                        override fun onSuccess() {
                            loginViewModel.saveRoleInPref(ROLE_USER)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onFailure(errorMessage: String?) {
                            showAlertDialog(WeakReference(this@LoginActivity), ERROR, errorMessage)
                        }

                    })
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }


}
