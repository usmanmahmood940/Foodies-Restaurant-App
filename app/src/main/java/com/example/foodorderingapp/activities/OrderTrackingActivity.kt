package com.example.foodorderingapp.activities

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.ORDER_ID
import com.example.foodorderingapp.Utils.Constants.ORDER_IN_DELIVERY
import com.example.foodorderingapp.Utils.Constants.ORDER_PLACED
import com.example.foodorderingapp.Utils.Constants.ORDER_PROCEED
import com.example.foodorderingapp.databinding.ActivityOrderTrackingBinding
import com.example.foodorderingapp.models.OrderDelivery
import com.example.foodorderingapp.viewModels.OrderTrackingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderTrackingBinding
    private lateinit var orderTrackingViewModel: OrderTrackingViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderTrackingViewModel = ViewModelProvider(this).get(OrderTrackingViewModel::class.java)

        orderTrackingViewModel.orderDelivery.observe(this) {
            when (it) {
                is CustomResponse.Success -> {
                    when (it.data?.status) {

                        ORDER_PROCEED -> {
                            binding.pbTracking.progress = 50
                            binding.tvOrderProceedCircle.background = ContextCompat.getDrawable(
                                this, R.drawable.shape_circle_btn_orange
                            )
                            binding.tvOrderProceed.text = ORDER_PROCEED

                        }
                        ORDER_IN_DELIVERY -> {

                        }
                    }

                }
                is CustomResponse.Error -> {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Error Message")
                    alertDialog.setMessage(it.errorMessage)
                    alertDialog.show()

                }
                else -> {}
            }
        }

        val orderId = sharedPreferences.getString(ORDER_ID, null)
        orderId?.let {
            orderTrackingViewModel.startObservingOrder(orderId)
            val orderDelivery = OrderDelivery()
            orderDelivery.proceedOrder()

            CoroutineScope(Dispatchers.IO).launch {
                orderTrackingViewModel.updateOrderStatus(orderId, orderDelivery)
            }

        }


    }
}