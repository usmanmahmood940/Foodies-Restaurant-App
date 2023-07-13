package com.example.foodorderingapp.Repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants.ORDDER_REFRENCE
import com.example.foodorderingapp.Utils.Helper
import com.example.foodorderingapp.Worker.UpdateSalesCountWorker
import com.example.foodorderingapp.models.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class OrderRepository @Inject constructor(private val workManager: WorkManager) {
    private val databaseReference = FirebaseDatabase.getInstance().getReference()
    private var valueEventListener: ValueEventListener? = null

    private val _orderDelivery = MutableLiveData<CustomResponse<OrderDelivery>>()
    val orderDelivery: LiveData<CustomResponse<OrderDelivery>>
        get() = _orderDelivery


    fun createOrder(order: Order, callback: (Boolean, Exception?, String?) -> Unit) {
        val id = databaseReference.push().key ?: Helper.generateRandomStringWithTime()
        order.orderId = id
        databaseReference.child(ORDDER_REFRENCE).child(id).setValue(order)
            .addOnSuccessListener {
                callback(true, null, order.orderId)
                order.cartItemList.forEach {
                    val inputData = workDataOf(
                        UpdateSalesCountWorker.KEY_FOOD_ITEM_ID to it.foodItem.id,
                        UpdateSalesCountWorker.KEY_QUANTITY to it.quantity
                    )

                    val request = OneTimeWorkRequestBuilder<UpdateSalesCountWorker>()
                        .setInputData(inputData)
                        .build()
                    workManager.enqueue(request)

                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception, null)
            }
    }

    fun updateOrderStatus(
        orderId: String,
        orderStatus: OrderDelivery,
        callback: (Boolean, Exception?) -> Unit,
    ) {
        databaseReference.child(ORDDER_REFRENCE).child(orderId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        databaseReference.child(ORDDER_REFRENCE).child(orderId).child("orderDelivery")
                            .setValue(orderStatus)
                            .addOnSuccessListener {
                                callback(true, null)
                            }
                            .addOnFailureListener { exception ->
                                callback(false, exception)
                            }
                    } else {
                        callback(false, null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun startObservingOrder(orderId: String) {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {

                    val tempOrderStatus: OrderDelivery? =  dataSnapshot.getValue(OrderDelivery::class.java)

                    _orderDelivery.value = CustomResponse.Success(tempOrderStatus)
                } catch (e: Exception) {
                    _orderDelivery.value = CustomResponse.Error(e.message.toString())
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                _orderDelivery.value =
                    CustomResponse.Error(databaseError.toException().message.toString())
            }
        }
        databaseReference.child(ORDDER_REFRENCE).child(orderId).child("orderDelivery")
            .addValueEventListener(valueEventListener as ValueEventListener)
    }

    fun stopObservingData(orderId: String) {
        valueEventListener?.let {
            databaseReference.child(ORDDER_REFRENCE).child(orderId).child("orderDelivery").removeEventListener(it)
        }
    }

}


