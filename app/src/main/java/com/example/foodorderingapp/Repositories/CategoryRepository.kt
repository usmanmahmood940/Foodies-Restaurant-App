package com.example.foodorderingapp.Repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants.CATEGORY_REFRENCE
import com.example.foodorderingapp.Utils.Helper.generateRandomStringWithTime
import com.example.foodorderingapp.models.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import javax.inject.Inject

class CategoryRepository @Inject constructor() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference().child(CATEGORY_REFRENCE)
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
                callback(false, exception) // Error: Failed to create Category
            }

    }

    fun getCategoriesList() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categories: MutableList<Category> = mutableListOf()
                try {
                    for (categorySnapshot in dataSnapshot.children) {

                        val category = categorySnapshot.getValue(Category::class.java)
                        if (category != null) {
                            categories.add(category)
                        }
                    }
                    categoriesLiveData.value = CustomResponse.Success(categories)
                }
                catch (e:Exception){
                    categoriesLiveData.value = CustomResponse.Error(e.message.toString())
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                categoriesLiveData.value =
                    CustomResponse.Error(databaseError.toException().message.toString())
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


