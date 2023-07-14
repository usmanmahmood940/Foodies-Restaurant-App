package com.example.foodorderingapp.models



data class Order(
    var orderId:String="",
    val customerInfo: CustomerInfo,
    val customerDeliveryInfo: DeliveryInfo,
    val cartItemList:List<CartItem>,
    val paymentMethod: String,
    val amounts: Amounts,
    val orderTracking : OrderTracking,
){
    constructor():this(
        "",
        CustomerInfo(),
        DeliveryInfo(),
        emptyList(),
        "",
        Amounts(),
        OrderTracking()
    )
}
