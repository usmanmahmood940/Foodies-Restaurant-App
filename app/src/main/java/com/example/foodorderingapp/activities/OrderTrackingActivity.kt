package com.example.foodorderingapp.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.RUNNING_ORDER
import com.example.foodorderingapp.databinding.ActivityOrderTrackingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderTrackingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityOrderTrackingBinding
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences.edit{
            putBoolean(RUNNING_ORDER, true)
            apply()
        }


    }
}