package com.example.foodorderingapp.models

data class Order(
    var orderId:String="",
    val customerInfo: CustomerInfo,
    val deliveryInfo: DeliveryInfo,
    val cartItemList:List<CartItem>,
    val paymentMethod: PaymentMethod,
    val totalAmount:Double
){
    constructor():this("", CustomerInfo(), DeliveryInfo(), emptyList(),PaymentMethod.CashOnDelivery(),0.0)
}
