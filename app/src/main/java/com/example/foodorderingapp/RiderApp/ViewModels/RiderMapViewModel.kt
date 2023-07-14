package com.example.foodorderingapp.RiderApp.ViewModels

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.Repositories.OrderRepository
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.CUSTOMER_LATITUDE
import com.example.foodorderingapp.Utils.Constants.CUSTOMER_LONGITUDE
import com.example.foodorderingapp.models.DeliveryInfo
import com.example.foodorderingapp.models.Order
import com.example.foodorderingapp.models.OrderTracking
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RiderMapViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    val runningOrder: LiveData<CustomResponse<Order>>
        get() = orderRepository.runningOrder

    init {
        checkRunningOrder()?.let {
            orderRepository.startTrackingOrder(it)
        }
    }


    fun updateOrderStatus(
        orderId: String,
        orderTracking: OrderTracking,
        callback: (Boolean, Exception?) -> Unit,
    ) {
        orderRepository.updateOrderStatus(orderId, orderTracking) { success, exception ->
            callback(success, exception)
        }
    }

    fun setRunningOrder(orderId: String?) {
        sharedPreferences.edit {
            putString(Constants.RIDER_RUNNING_ORDER, orderId)
            apply()
        }
    }

    fun checkRunningOrder(): String? {
        return sharedPreferences.getString(Constants.RIDER_RUNNING_ORDER, null)
    }

    fun stopTrackingOrder(orderId: String) {
        orderRepository.stopTrackingOrder(orderId)
    }

    fun getCustomerLatLng(): DeliveryInfo? {

        val latitude =   sharedPreferences.getString(CUSTOMER_LATITUDE, null)?.toDouble()
        val longitude =   sharedPreferences.getString(CUSTOMER_LONGITUDE,null)?.toDouble()

        if(latitude != null && longitude != null){
           return  DeliveryInfo(
               locationLatitude = latitude,
               locationLongitude = longitude
           )
        }
        return null

    }


}