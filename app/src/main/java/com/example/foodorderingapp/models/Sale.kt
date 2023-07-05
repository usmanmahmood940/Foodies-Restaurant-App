package com.example.foodorderingapp.models

data class Sale(
    val saleId:String,
    val cartItemList:List<FoodItem>,
    val totalAmount:Double
)
