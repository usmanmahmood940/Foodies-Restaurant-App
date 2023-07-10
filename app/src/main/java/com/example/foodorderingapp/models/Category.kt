package com.example.foodorderingapp.models



data class Category(
    val id:String ="",
    val name:String,
    val icon:String = "",

    )  {
    constructor() : this("", "", "")
}
