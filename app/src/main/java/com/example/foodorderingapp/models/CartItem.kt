package com.example.foodorderingapp.models

data class CartItem(
    val foodItem: FoodItem,
    var quantity:Int,
    var totalAmount:Double
) {

    fun updateTotalAmount() {
        totalAmount = foodItem.price * quantity
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CartItem
        return foodItem.id == other.foodItem.id
    }

    constructor():this(FoodItem(),0,0.0)

}