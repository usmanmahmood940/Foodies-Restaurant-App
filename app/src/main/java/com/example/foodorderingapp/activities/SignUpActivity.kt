package com.example.foodorderingapp.activities

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.R
import com.example.foodorderingapp.viewModels.SignUpViewModel
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.ERROR
import com.example.foodorderingapp.Utils.Constants.INFORMATION
import com.example.foodorderingapp.Utils.Helper.getImprovedBitmap
import com.example.foodorderingapp.Utils.Helper.isValidEmail
import com.example.foodorderingapp.Utils.Helper.showAlertDialog
import com.example.foodorderingapp.Utils.Helper.showError
import com.example.foodorderingapp.Utils.Helper.toUri
import com.example.foodorderingapp.Utils.Helper.togglePasswordVisibility
import com.example.foodorderingapp.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.ref.WeakReference

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var signupListener: CustomSuccessFailureListener
    private var imageUri: Uri? = null

    private val pickImage: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.tvUploadImage.visibility = View.GONE
                binding.ivProfileImage.setImageURI(uri)

            }
        }

    private val takePhotoLauncher: ActivityResultLauncher<Void?> =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                binding.tvUploadImage.visibility = View.GONE
                binding.ivProfileImage.setImageBitmap(bitmap.getImprovedBitmap())
                imageUri = bitmap.toUri(applicationContext)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        initializeSignupListener()
        binding.btnSignup.setOnClickListener {
            signup()
        }

        binding.ivEye.setOnClickListener {
            binding.etPassword.togglePasswordVisibility()
        }

        binding.ivProfileImage.setOnClickListener {
            showDialog()
        }
        binding.tvUploadImage.setOnClickListener {
            showDialog()
        }

    }

    private fun initializeSignupListener() {
        signupListener = object : CustomSuccessFailureListener {
            override fun onSuccess() {
                showAlertDialog(
                    WeakReference(this@SignUpActivity),
                    INFORMATION,
                    "Please verify your email",
                    object : OnDismissListener {
                        override fun onDismiss(dialog: DialogInterface?) {
                            finish()
                        }
                    }
                )

            }
            override fun onFailure(errorMessage: String?) {
                binding.apply {
                    svSignup.alpha = 1f
                    btnSignup.isEnabled = true
                    progressBarSignup.visibility = View.GONE
                }
                showAlertDialog(
                    WeakReference(this@SignUpActivity),
                    Constants.ERROR,
                    errorMessage
                )

            }

        }
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_options, null)
        val openCameraButton = dialogView.findViewById<Button>(R.id.btn_open_camera)
        val chooseGalleryButton = dialogView.findViewById<Button>(R.id.btn_choose_gallery)

        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialog)
            .setView(dialogView)

        val dialog = dialogBuilder.create()


        openCameraButton.setOnClickListener {
            lauchCamera()
            dialog.dismiss()
        }

        chooseGalleryButton.setOnClickListener {
            pickImageFromGallery()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun lauchCamera() {
        takePhotoLauncher.launch(null)
    }

    private fun pickImageFromGallery() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private fun signup() {
        with(binding) {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val name = etName.text.toString().trim()
            val mobileNumber = etMobileNum.text.toString().trim()

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
                name.isBlank() -> {
                    etName.showError(getString(R.string.field_required_error))
                }
                mobileNumber.isBlank() -> {
                    etMobileNum.showError(getString(R.string.field_required_error))
                }
                (imageUri == null) -> {
                    showAlertDialog(
                        WeakReference(this@SignUpActivity),
                        ERROR,
                        "Please Upload Image"
                    )
                }
                else -> {
                    binding.apply {
                        svSignup.alpha = 0.5f
                        btnSignup.isEnabled = false
                        progressBarSignup.visibility = View.VISIBLE
                    }
                    signUpViewModel.signUp(
                        email,
                        password,
                        name,
                        mobileNumber,
                        imageUri!!,
                        signupListener)
                }
            }
        }

    }



}