package com.example.foodorderingapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import com.example.foodorderingapp.RiderApp.RiderHomeActivity
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
        if (!firstTimeOpen) {
            val intent = getStartActivityIntent()
            startActivity(intent)
            finish()
        }

        binding.btnStart.setOnClickListener {
            sharedPreferences.edit {
                putBoolean(FIRST_TIME_OPEN, false)
                apply()
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getStartActivityIntent(): Intent {
        return if (auth.currentUser != null) {
            if (auth.currentUser?.uid == "E5ODMiUXLkahCFBSv0WRh6q2jh83") {
                Intent(this, RiderHomeActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
        } else {
            Intent(this, LoginActivity::class.java)
        }
    }
}
