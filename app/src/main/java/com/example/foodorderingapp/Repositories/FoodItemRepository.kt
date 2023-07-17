package com.example.foodorderingapp.Repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.FOOD_ITEM_REFRENCE
import com.example.foodorderingapp.Utils.Helper.generateRandomStringWithTime
import com.example.foodorderingapp.models.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class FoodItemRepository  @Inject constructor() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference().child(FOOD_ITEM_REFRENCE)
    private var valueEventListener: ValueEventListener? = null

    private val foodItemLiveData = MutableLiveData<CustomResponse<List<FoodItem>>>()
    val foodItemList: LiveData<CustomResponse<List<FoodItem>>>
        get() = foodItemLiveData

    init {
        foodItemLiveData.value = CustomResponse.Loading()
    }



    fun createFoodItem(foodItem: FoodItem, callback: (Boolean, Exception?) -> Unit) {

        val id = databaseReference.push().key ?: generateRandomStringWithTime()
        val newFoodItem = FoodItem(
            id,
            foodItem.title,
            foodItem.image,
            foodItem.description,
            foodItem.price,
            foodItem.categoryId
        )
        databaseReference.child(id).setValue(newFoodItem)
            .addOnSuccessListener {
                callback(true, null) // Success: FoodItem created
            }
            .addOnFailureListener { exception ->
                callback(false, exception) // Error: Failed to create FoodItem
            }

    }

    fun getFoodItemList() {
        foodItemLiveData.value = CustomResponse.Loading()
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val foodItemList: MutableList<FoodItem> = mutableListOf()
                for (foodItemSnapshot in dataSnapshot.children) {
                    val foodItem = foodItemSnapshot.getValue(FoodItem::class.java)
                    if (foodItem != null) {
                        foodItemList.add(foodItem)
                    }
                }
                foodItemLiveData.value = CustomResponse.Success(foodItemList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                foodItemLiveData.value = CustomResponse.Error(databaseError.toException().message.toString())
            }
        }
        databaseReference.addValueEventListener(valueEventListener as ValueEventListener)
    }

    fun stopObservingData() {
        valueEventListener?.let {
            databaseReference.removeEventListener(it)
        }
    }

}


