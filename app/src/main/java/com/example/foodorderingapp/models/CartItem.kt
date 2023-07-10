package com.example.foodorderingapp.models

data class CartItem(
    val foodItem: FoodItem,
    var quantity:Int,
    var totalAmount:Double
) {

    fun updateTotalAmount() {
        totalAmount = foodItem.price * quantity
    }

}