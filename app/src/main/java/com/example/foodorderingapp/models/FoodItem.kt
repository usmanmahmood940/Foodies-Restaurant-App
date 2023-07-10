package com.example.foodorderingapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodItem(
    val id:String="",
    val title: String="",
    val image: String="",
    val description: String="",
    val price: Double=0.0,
    val categoryId: String="",
    val salesCount:Int = 0,
) : Parcelable
