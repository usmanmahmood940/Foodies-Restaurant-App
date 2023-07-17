package com.example.foodorderingapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodorderingapp.R
import com.example.foodorderingapp.RiderApp.RiderHomeActivity
import com.example.foodorderingapp.Utils.Helper.isValidEmail
import com.example.foodorderingapp.Utils.Helper.showError
import com.example.foodorderingapp.databinding.ActivityLoginBinding
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.firebase.auth.AuthCredential

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        auth.currentUser?.getIdToken(false)?.addOnSuccessListener {
            Toast.makeText(this, it.token.toString(), Toast.LENGTH_LONG).show()
        }


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
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this@LoginActivity) { task ->
                                if (task.isSuccessful) {
                                    val destinationClass =
                                        if (auth.currentUser?.uid == "E5ODMiUXLkahCFBSv0WRh6q2jh83") {
                                            RiderHomeActivity::class.java
                                        } else {
                                            MainActivity::class.java
                                        }
                                    startActivity(Intent(this@LoginActivity, destinationClass))
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Firebase sign-in failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                    }
                }
            }
        }



        binding.btnGoogleLogin.setOnClickListener {
            signInGoogle()
        }

        binding.ivEye.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        val inputType = binding.etPassword.inputType
        val newInputType = if (inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.etPassword.inputType = newInputType
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                firebaseAuthentication(credentials)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthentication(credentials: AuthCredential) {
        auth.signInWithCredential(credentials)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Firebase sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
