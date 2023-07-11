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
    private val cartManager: CartManager
) : ViewModel() {

    val cartItemList: LiveData<MutableList<CartItem>>
        get() = cartManager.cartItemList

    fun updateCart(){
        cartManager.updateSharedPref()
    }

    fun removeItem(position:Int){
        cartManager.removeFromCart(position)
    }




}