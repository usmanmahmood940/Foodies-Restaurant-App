package com.example.foodorderingapp.models

import kotlinx.serialization.Serializable


sealed class PaymentMethod(val method:String="") {

    class CashOnDelivery(method: String):PaymentMethod(method = method)
    class CreditDebitCard(method: String):PaymentMethod(method = method)
}