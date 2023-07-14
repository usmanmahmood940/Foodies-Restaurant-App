package com.example.foodorderingapp.models

import com.example.foodorderingapp.Utils.Constants.ORDER_IN_DELIVERY
import com.example.foodorderingapp.Utils.Constants.ORDER_PLACED
import com.example.foodorderingapp.Utils.Constants.ORDER_PROCEED

data class OrderTracking(
    var status:String = "",
    val driverInfo: DriverInfo? = null,
    val deliveryInfo: DeliveryInfo? = null
) {

    fun placeOrder(){
        status = ORDER_PLACED
    }

    fun proceedOrder(){
        status = ORDER_PROCEED
    }

    fun sendForDelivery(){
        status = ORDER_IN_DELIVERY
    }


}


