package com.example.foodorderingapp.RiderApp

import com.example.foodorderingapp.models.Order

interface RiderOrderConfirmClickListener {
    fun onConfirm(order: Order)
}