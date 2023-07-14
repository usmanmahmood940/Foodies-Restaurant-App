package com.example.foodorderingapp.viewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.Repositories.OrderRepository
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.models.Order
import com.example.foodorderingapp.models.OrderTracking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class RiderHomeViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val sharedPreferences: SharedPreferences
): ViewModel() {

    val newOrdersList: LiveData<CustomResponse<List<Order>>>
        get() = orderRepository.proceededOrderList

    init {
        orderRepository.startObservingProceededOrders()
//        sharedPreferences.edit{
//            putBoolean(Constants.RIDER_RUNNING_ORDER, true)
//            apply()
//        }
    }


    suspend fun updateOrderStatus(orderId:String,orderStatus: OrderTracking){
        delay(5000)
        orderRepository.updateOrderStatus(orderId,orderStatus){ success,exception ->
            if(success){
                Log.d(Constants.MY_TAG,"Order Status Updated")
            }
            else{
                exception?.let {
                    Log.e(Constants.MY_TAG,exception?.message.toString())
                    return@updateOrderStatus
                }
                Log.e(Constants.MY_TAG,"Order Does not exist")

            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        orderRepository.stopObservingProceededOrders()

    }


}