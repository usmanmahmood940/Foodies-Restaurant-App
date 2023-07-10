package com.example.foodorderingapp.models

data class CustomerInfo(
    val name:String ,
    val email:String,
    val phoneNumner:String
){
    constructor() : this("", "", "")
}