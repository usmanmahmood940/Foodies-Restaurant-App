package com.example.foodorderingapp.RiderApp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.databinding.ActivityRiderHomeBinding
import com.example.foodorderingapp.models.Order
import com.example.foodorderingapp.viewModels.HomeViewModel
import com.example.foodorderingapp.viewModels.RiderHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RiderHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiderHomeBinding
    private lateinit var riderHomeViewModel: RiderHomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiderHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        riderHomeViewModel = ViewModelProvider(this).get(RiderHomeViewModel::class.java)
        val adapter = RiderOrderAdapter(listener = object : RiderOrderConfirmClickListener {
            override fun onConfirm(order: Order) {

            }
        })
        binding.rlNewOrders.layoutManager = LinearLayoutManager(this)
        binding.rlNewOrders.adapter = adapter

        riderHomeViewModel.newOrdersList.observe(this) {
            when (it) {
                is CustomResponse.Loading -> {
                    binding.rlNewOrders.visibility = View.INVISIBLE
                    binding.progressBarRiderOrders.visibility = View.VISIBLE

                }
                is CustomResponse.Success -> {
                    if(it.data != null) {
                        binding.rlNewOrders.visibility = View.VISIBLE
                        binding.progressBarRiderOrders.visibility = View.GONE
                        adapter.setList(it.data)
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
    }
}