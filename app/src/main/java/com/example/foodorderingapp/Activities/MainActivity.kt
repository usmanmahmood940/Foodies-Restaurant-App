package com.example.foodorderingapp.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodorderingapp.CartManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var cartManager: CartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!
            .findNavController()

        binding.bottomNavigationView.setupWithNavController(navController)


        binding.btnCart.setOnClickListener {
            binding.bottomNavigationView.menu.findItem(R.id.nav_cart)?.isEnabled = true
            binding.bottomNavigationView.selectedItemId = R.id.nav_cart
            binding.bottomNavigationView.menu.findItem(R.id.nav_cart)?.isEnabled = false
        }
        binding.btnCart.count = cartManager.getCartCount()

        cartManager.setCartChangeListener(object :CartManager.CartChangeListener{
            override fun onCartChanged(cartCount: Int) {
                binding.btnCart.count = cartCount
            }

        })


    }


}