package com.example.foodorderingapp.Repositories

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.foodorderingapp.Utils.Constants.ORDDER_REFRENCE
import com.example.foodorderingapp.Utils.Helper
import com.example.foodorderingapp.Worker.UpdateSalesCountWorker
import com.example.foodorderingapp.models.Category
import com.example.foodorderingapp.models.FoodItem
import com.example.foodorderingapp.models.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class OrderRepository  @Inject constructor(private val workManager: WorkManager) {
    private val databaseReference = FirebaseDatabase.getInstance().getReference()

    fun createOrder(order: Order, callback: (Boolean, Exception?) -> Unit) {
        val id = databaseReference.push().key ?: Helper.generateRandomStringWithTime()
        order.orderId = id
        databaseReference.child(ORDDER_REFRENCE).child(id).setValue(order)
            .addOnSuccessListener {
                callback(true, null)
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
                callback(false, exception)
            }

    }

}


