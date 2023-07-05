package com.example.foodorderingapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.CartManager
import com.example.foodorderingapp.Repositories.FoodItemRepository
import com.example.foodorderingapp.models.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CartViewModel  @Inject constructor(
    private val foodItemRepository: FoodItemRepository,
    private val cartManager: CartManager
) : ViewModel() {

    val cartItemList: LiveData<MutableList<CartItem>>
        get() = cartManager.cartItemList

    var tax:Double=0.0
    var totalItemAmount:Double = 0.0
    var totalAMount:Double = 0.0
    var deliveryCharges:Int
    init{
        deliveryCharges = 200
        updateAmounts()
    }

    fun updateCart(){
        cartManager.updateSharedPref()
    }

    fun removeItem(position:Int){
        cartManager.removeFromCart(position)
    }


    fun updateAmounts(){
        totalItemAmount = cartItemList.value?.sumOf { it.totalAmount }!!
        tax = (totalItemAmount * 17) / 100
        totalAMount = totalItemAmount + tax + deliveryCharges

    }

}