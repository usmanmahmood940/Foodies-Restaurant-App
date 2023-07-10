package com.example.foodorderingapp.models

data class DeliveryInfo(
    val locationLatitude:Double,
    val locationLongitude:Double
){
    constructor():this(0.0,0.0)
}
