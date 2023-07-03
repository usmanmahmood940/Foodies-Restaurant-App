package com.example.foodorderingapp.Repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.models.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import javax.inject.Inject

class CategoryRepository  @Inject constructor() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference().child("Category")
    private var valueEventListener: ValueEventListener? = null

    private val categoriesLiveData = MutableLiveData<CustomResponse<List<Category>>>()
    val categoriesList: LiveData<CustomResponse<List<Category>>>
        get() = categoriesLiveData

    init {
        categoriesLiveData.value = CustomResponse.Loading()
    }

  

    fun createCategory(categoryName: String, callback: (Boolean, Exception?) -> Unit) {

        val id = databaseReference.push().key ?: generateRandomStringWithTime()
        val newCategory = Category(id, categoryName, "")
        databaseReference.child(id).setValue(newCategory)
            .addOnSuccessListener {
                callback(true, null) // Success: Category created
            }
            .addOnFailureListener { exception ->
                Log.d("usman error",exception.message.toString())
                callback(false, exception) // Error: Failed to create Category
            }

    }

    fun getCategoriesList() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categories: MutableList<Category> = mutableListOf()
                for (categorySnapshot in dataSnapshot.children) {
                    val category = categorySnapshot.getValue(Category::class.java)
                    if (category != null) {
                        categories.add(category)
                    }
                }
                categoriesLiveData.value = CustomResponse.Success(categories)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                categoriesLiveData.value = CustomResponse.Error(databaseError.toException().message.toString())
            }
        }
        databaseReference.addValueEventListener(valueEventListener as ValueEventListener)
    }



    fun generateRandomStringWithTime(): String {
        val timestamp = System.currentTimeMillis()
        val randomString = UUID.randomUUID().toString()
        return "$randomString-$timestamp"
    }

}


