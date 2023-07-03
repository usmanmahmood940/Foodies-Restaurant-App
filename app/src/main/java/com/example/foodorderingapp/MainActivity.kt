package com.example.foodorderingapp

import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodorderingapp.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  lateinit var binding: ActivityMainBinding
  private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        binding.btnCart.count = 5

//        binding.btnCart.viewTreeObserver.addOnGlobalLayoutListener(@ExperimentalBadgeUtils object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                val badgeDrawable = BadgeDrawable.create(this@MainActivity)
//                badgeDrawable.number = 2
//                badgeDrawable.horizontalOffset = 30
//                badgeDrawable.verticalOffset = 20
//
//
//                BadgeUtils.attachBadgeDrawable(badgeDrawable, binding.btnCart, null)
//
//                binding.btnCart.viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//        })

    }



}