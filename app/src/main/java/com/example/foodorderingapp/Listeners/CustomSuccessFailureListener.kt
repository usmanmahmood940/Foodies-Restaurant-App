package com.example.foodorderingapp.Listeners

interface CustomSuccessFailureListener {

    fun onSuccess()

    fun onFailure(errorMessage:String?)
}