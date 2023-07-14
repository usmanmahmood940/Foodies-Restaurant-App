package com.example.foodorderingapp.RiderApp.ViewModels

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.Repositories.OrderRepository
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants.CUSTOMER_LATITUDE
import com.example.foodorderingapp.Utils.Constants.CUSTOMER_LONGITUDE
import com.example.foodorderingapp.Utils.Constants.RIDER_RUNNING_ORDER
import com.example.foodorderingapp.models.DeliveryInfo
import com.example.foodorderingapp.models.Order
import com.example.foodorderingapp.models.OrderTracking
import dagger.hilt.android.lifecycle.HiltViewModel
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


     fun updateOrderStatus(orderId:String, orderTracking: OrderTracking, callback: (Boolean, Exception?) -> Unit,){
        orderRepository.updateOrderTracking(orderId,orderTracking){ success, exception ->
           callback(success,exception)
        }
    }

    fun setRunningOrder(orderId:String){
        sharedPreferences.edit {
            putString(RIDER_RUNNING_ORDER,orderId)
            apply()
        }
    }

    fun setCustomerLatLng(deliveryInfo: DeliveryInfo){
        sharedPreferences.edit {
            putString(CUSTOMER_LATITUDE,deliveryInfo.locationLatitude.toString())
            putString(CUSTOMER_LONGITUDE,deliveryInfo.locationLongitude.toString())
            apply()
        }
    }
    fun checkRunningOrder():String?{
        return sharedPreferences.getString(RIDER_RUNNING_ORDER,null)
    }

    override fun onCleared() {
        super.onCleared()
        orderRepository.stopObservingProceededOrders()

    }


}