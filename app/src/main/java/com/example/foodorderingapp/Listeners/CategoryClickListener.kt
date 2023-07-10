package com.example.foodorderingapp.Listeners

import com.example.foodorderingapp.models.Category

interface CategoryClickListener {
    fun onItemClick(category:Category)
    fun onItemDeselect()
}