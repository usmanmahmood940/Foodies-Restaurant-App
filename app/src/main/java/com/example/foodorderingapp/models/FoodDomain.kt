package com.example.foodorderingapp.models

data class FoodDomain(
    val title:String,
    val image:Int,
    val description:String,
    val price:Double,
    val numberInCart:Int = 0
)
