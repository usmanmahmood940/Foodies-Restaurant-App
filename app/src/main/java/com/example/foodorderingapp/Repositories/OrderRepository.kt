package com.example.foodorderingapp.Repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants.ORDER_REFRENCE
import com.example.foodorderingapp.Utils.Constants.ORDER_DELIVERY_INFO_REFRENCE
import com.example.foodorderingapp.Utils.Constants.ORDER_PROCEED
import com.example.foodorderingapp.Utils.Constants.ORDER_TRACKING_REFRENCE
import com.example.foodorderingapp.Utils.Constants.STATUS_REFRENCE
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
    private var valueEventListenerOrdersList: ValueEventListener? = null

    private val _runningOrder = MutableLiveData<CustomResponse<Order>>(CustomResponse.Loading())
    val runningOrder: LiveData<CustomResponse<Order>>
        get() = _runningOrder

    private val _proceededOrderList =
        MutableLiveData<CustomResponse<List<Order>>>(CustomResponse.Loading())
    val proceededOrderList: LiveData<CustomResponse<List<Order>>>
        get() = _proceededOrderList

    fun createOrder(order: Order, callback: (Boolean, Exception?, String?) -> Unit) {
        val id = databaseReference.push().key ?: Helper.generateRandomStringWithTime()
        order.orderId = id
        databaseReference.child(ORDER_REFRENCE).child(id).setValue(order)
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

    fun updateOrderTracking(
        orderId: String,
        orderStatus: OrderTracking,
        callback: (Boolean, Exception?) -> Unit,
    ) {
        val orderReference = databaseReference.child(ORDER_REFRENCE).child(orderId)

        orderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    orderReference.child(ORDER_TRACKING_REFRENCE)
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
                // Handle any errors that occurred during the query
            }
        })
    }

    fun updateRiderLocation(orderId: String, deliveryInfo: DeliveryInfo) {
        val orderReference = databaseReference.child(ORDER_REFRENCE).child(orderId)

        orderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val deliveryInfoReference = orderReference.child(ORDER_TRACKING_REFRENCE)
                        .child(ORDER_DELIVERY_INFO_REFRENCE)
                    deliveryInfoReference.setValue(deliveryInfo)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occurred during the query
            }
        })
    }

    fun startTrackingOrder(orderId: String) {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val tempOrder: Order? = dataSnapshot.getValue(Order::class.java)
                    _runningOrder.value = CustomResponse.Success(tempOrder)
                } catch (e: Exception) {
                    _runningOrder.value = CustomResponse.Error(e.message.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _runningOrder.value =
                    CustomResponse.Error(databaseError.toException().message.toString())
            }
        }
        databaseReference.child(ORDER_REFRENCE).child(orderId)
            .addValueEventListener(valueEventListener as ValueEventListener)
    }

    fun stopTrackingOrder(orderId: String) {
        valueEventListener?.let {
            databaseReference.child(ORDER_REFRENCE).child(orderId).child(ORDER_TRACKING_REFRENCE)
                .removeEventListener(it)
        }
    }

    fun startObservingProceededOrders() {
        valueEventListenerOrdersList = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val orderList = mutableListOf<Order>()

                try {
                    for (orderSnapshot in dataSnapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        order?.let {
                            orderList.add(it)
                        }
                    }

                    _proceededOrderList.value = CustomResponse.Success(orderList)
                } catch (e: Exception) {
                    _proceededOrderList.value = CustomResponse.Error(e.message.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occurred during the query
            }
        }

        databaseReference.child(ORDER_REFRENCE)
            .orderByChild("$ORDER_TRACKING_REFRENCE/$STATUS_REFRENCE")
            .equalTo(ORDER_PROCEED)
            .addValueEventListener(valueEventListenerOrdersList as ValueEventListener)
    }

    fun stopObservingProceededOrders() {
        valueEventListenerOrdersList?.let {
            databaseReference.child(ORDER_REFRENCE).removeEventListener(it)
        }
    }

    fun updateOrderStatus(orderId: String, orderStatus: String, callback: (Boolean, Exception?) -> Unit) {
        val orderReference = databaseReference.child(ORDER_REFRENCE).child(orderId)

        orderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        val statusRef = orderReference.child(ORDER_TRACKING_REFRENCE)
                            .child(STATUS_REFRENCE)
                        statusRef.setValue(orderStatus)
                        callback(true, null)
                    } catch (e: Exception) {
                        callback(false, e)
                    }
                } else {
                    callback(false, null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, null)
            }
        })
    }
}


