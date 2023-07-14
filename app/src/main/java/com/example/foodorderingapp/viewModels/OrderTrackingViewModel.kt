package com.example.foodorderingapp.viewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.Repositories.OrderRepository
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.MY_TAG
import com.example.foodorderingapp.models.OrderTracking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val sharedPreferences: SharedPreferences
):ViewModel() {

    val orderDelivery: LiveData<CustomResponse<OrderTracking>>
        get() = orderRepository.orderDelivery

    init {
        sharedPreferences.edit{
            putBoolean(Constants.RUNNING_ORDER, true)
            apply()
        }
    }

    fun startObservingOrder(orderId: String){
        orderRepository.startTrackingOrder(orderId)
    }

    suspend fun updateOrderStatus(orderId:String,orderStatus: OrderTracking){
        delay(5000)
        orderRepository.updateOrderStatus(orderId,orderStatus){ success,exception ->
            if(success){
               Log.d(MY_TAG,"Order Status Updated")
            }
            else{
                exception?.let {
                    Log.e(MY_TAG,exception?.message.toString())
                    return@updateOrderStatus
                }
                Log.e(MY_TAG,"Order Does not exist")

            }

        }
    }


}