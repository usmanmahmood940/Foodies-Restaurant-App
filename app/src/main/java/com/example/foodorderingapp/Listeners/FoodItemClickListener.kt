package com.example.foodorderingapp.Listeners

import com.example.foodorderingapp.models.FoodItem

interface FoodItemClickListener {
    fun onAddClicked(foodItem: FoodItem)
}