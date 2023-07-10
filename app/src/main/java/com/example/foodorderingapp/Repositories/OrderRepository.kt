package com.example.foodorderingapp.Repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Helper
import com.example.foodorderingapp.models.Category
import com.example.foodorderingapp.models.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class OrderRepository  @Inject constructor() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference().child("Order")

    fun createOrder(order: Order, callback: (Boolean, Exception?) -> Unit) {

        val id = databaseReference.push().key ?: Helper.generateRandomStringWithTime()
        order.orderId = id
        databaseReference.child(id).setValue(order)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                callback(false, exception)
            }

    }







}


