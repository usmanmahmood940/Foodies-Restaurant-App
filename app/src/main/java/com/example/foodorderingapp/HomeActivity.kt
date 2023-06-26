package com.example.foodorderingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodorderingapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

  lateinit var binding: ActivityHomeBinding
  private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!.findNavController()

        binding.bottomNavigationView.setupWithNavController(navController)


        binding.btnCart.setOnClickListener {
            binding.bottomNavigationView.menu.findItem(R.id.nav_cart)?.isEnabled = true
            binding.bottomNavigationView.selectedItemId = R.id.nav_cart
            binding.bottomNavigationView.menu.findItem(R.id.nav_cart)?.isEnabled = false
        }
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            binding.bottomNavigationView.menu.findItem(R.id.nav_cart)?.isEnabled = false
//        }


       

    }

    private fun popBackStack(id: Int?) {
        if(id != R.id.homeFragment) {
            navController.popBackStack()
        }
    }


}