package com.example.foodorderingapp.Worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.foodorderingapp.Repositories.OrderRepository
import com.example.foodorderingapp.Utils.Constants.FOOD_ITEM_REFRENCE
import com.example.foodorderingapp.models.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateSalesCountWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result  {
        return try {
            // Retrieve the input data
            val foodItemId = inputData.getString(KEY_FOOD_ITEM_ID)
            val quantity = inputData.getInt(KEY_QUANTITY, 0)

            foodItemId?.let {
              updateSalesCount(foodItemId, quantity)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_FOOD_ITEM_ID = "FoodItemId"
        const val KEY_QUANTITY = "Quantity"
    }

    fun updateSalesCount(foodItemId: String, quantity: Int) {
        val foodItemRefrence = FirebaseDatabase.getInstance().getReference().child(FOOD_ITEM_REFRENCE)
        foodItemRefrence.child(foodItemId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foodItem = snapshot.getValue(FoodItem::class.java)
                foodItem?.apply {
                    salesCount = salesCount + quantity
                    foodItemRefrence.child(id).setValue(foodItem)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
}
