package com.example.foodorderingapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Amounts(
    var totalItemAmount:Double=0.0,
    var taxAmount:Double=0.0,
    var taxPercent:Double = 17.0,
    var totalAmount:Double = 0.0,
    var deliveryCharges:Int = 5

):Parcelable{
    fun updateTotalItemAmount(amount: Double) {
        totalItemAmount = amount
        updateAmounts()

    }
    fun updateAmounts(){
        taxAmount = (taxPercent * totalItemAmount) / 100
        totalAmount = totalItemAmount + taxAmount + deliveryCharges
    }

}
