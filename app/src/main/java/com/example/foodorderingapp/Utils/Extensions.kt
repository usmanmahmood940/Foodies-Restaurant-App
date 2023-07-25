package com.example.foodorderingapp.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.InputType
import android.widget.EditText
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Extensions {
    fun String.isValidEmail(): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return Regex(emailRegex).matches(this)
    }

    fun EditText.showError(errorMsg: String) {
        error = errorMsg
        requestFocus()
    }

    fun EditText.togglePasswordVisibility() {
        val inputType = inputType
        val newInputType =
            if (inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        setInputType(newInputType)
    }


    fun Bitmap.toUri(context: Context): Uri? {
        // Create a file to save the bitmap
        val file = File(context.cacheDir, "profileImage.jpg")
        try {
            val outputStream = FileOutputStream(file)
            compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Generate a content URI for the file using FileProvider
            return FileProvider.getUriForFile(context, "com.example.fileprovider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun Bitmap.getImprovedBitmap(): Bitmap {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

}