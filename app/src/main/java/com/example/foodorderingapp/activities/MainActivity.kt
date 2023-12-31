package com.example.foodorderingapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodorderingapp.CartManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.FULL_SCREEN_OPACITY
import com.example.foodorderingapp.Utils.Constants.HALF_SCREEN_OPACITY
import com.example.foodorderingapp.Utils.Constants.RUNNING_ORDER
import com.example.foodorderingapp.Utils.Helper.showAlertDialog
import com.example.foodorderingapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var cartManager: CartManager
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()
        setupCartButton()

        cartManager.setCartChangeListener(object : CartManager.CartChangeListener {
            override fun onCartChanged(cartCount: Int) {
                binding.btnCart.count = cartCount
            }
        })
    }

    private fun setupNavController() {
        navController = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!
            .findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupCartButton() {
        binding.btnCart.setOnClickListener {
            binding.bottomNavigationView.apply {
                menu.findItem(R.id.nav_cart)?.isEnabled = true
                selectedItemId = R.id.nav_cart
                menu.findItem(R.id.nav_cart)?.isEnabled = false
            }
        }
        binding.btnCart.count = cartManager.getCartCount()
    }

    fun hideShowBottomNav() {
        binding.coordinatorLayout.visibility = if (binding.coordinatorLayout.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun checkRunningOrder() {
        if (sharedPreferences.getBoolean(RUNNING_ORDER, false)) {
            showAlertDialog(
                WeakReference(this@MainActivity,),
                getString(R.string.information),
                getString(R.string.view_running_order),
                positiveListener = object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        startActivity(Intent(this@MainActivity, OrderTrackingActivity::class.java))
                    }
                },
                positiveButtonText = getString(R.string.view)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        checkRunningOrder()
    }
}
