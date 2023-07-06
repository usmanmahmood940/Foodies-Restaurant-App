package com.example.foodorderingapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodorderingapp.Activities.LoginActivity
import com.example.foodorderingapp.Activities.MainActivity
import com.example.foodorderingapp.Utils.Constants.FIRST_TIME_OPEN
import com.example.foodorderingapp.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firstTimeOpen = sharedPreferences.getBoolean(FIRST_TIME_OPEN, true)
        if (firstTimeOpen == false) {
            var intent = Intent(this, LoginActivity::class.java)
            auth.currentUser?.let {
                intent = Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
            return
      
        }
        binding.btnStart.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean(FIRST_TIME_OPEN, false)
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}