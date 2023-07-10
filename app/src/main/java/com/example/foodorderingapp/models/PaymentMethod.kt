package com.example.foodorderingapp.models

sealed class PaymentMethod {
    class CashOnDelivery():PaymentMethod()
    class CreditDebitCard():PaymentMethod()
}