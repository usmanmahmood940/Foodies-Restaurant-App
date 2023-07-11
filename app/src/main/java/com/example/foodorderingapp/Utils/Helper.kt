package com.example.foodorderingapp.Utils

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import java.util.*

object Helper {
    
    fun generateRandomStringWithTime(): String {
        val timestamp = System.currentTimeMillis()
        val randomString = UUID.randomUUID().toString()
        return "$randomString-$timestamp"
    }

    fun String.isValidEmail(): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return Regex(emailRegex).matches(this)
    }

}