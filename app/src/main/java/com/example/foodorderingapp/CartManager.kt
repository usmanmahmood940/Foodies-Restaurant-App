package com.example.foodorderingapp

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants.CART
import com.example.foodorderingapp.Utils.Constants.CART_COUNT
import com.example.foodorderingapp.models.CartItem
import com.example.foodorderingapp.models.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartManager(private val sharedPreferences: SharedPreferences) {

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    private var cartChangeListener: CartChangeListener? = null
    private val _cartItemList = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItemList :LiveData<MutableList<CartItem>>
        get()  = _cartItemList


    init {
        val cartJson = sharedPreferences.getString(CART, null)
        if (!cartJson.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<CartItem>?>() {}.type
            _cartItemList.value = Gson().fromJson<MutableList<CartItem>>(cartJson, type)
        }
    }

    interface CartChangeListener {
        fun onCartChanged(cartCount: Int)
    }

    fun getCartCount(): Int {
        return sharedPreferences.getInt(CART_COUNT, 0)
    }

    fun addToCart(cartItem: CartItem) {
        if (_cartItemList.value?.contains(cartItem) == true) {
            val existingCartItem = _cartItemList.value?.find { it == cartItem }
            existingCartItem?.let {
                it.quantity += cartItem.quantity
                it.updateTotalAmount()
            }
        } else {
            _cartItemList.value?.add(cartItem)
        }

        updateSharedPref()
    }

    fun removeFromCart(cartItem: CartItem) {
        _cartItemList.value?.remove(cartItem)

        updateSharedPref()


    }
    fun removeFromCart(position: Int) {
        _cartItemList.value?.removeAt(position)
        _cartItemList.value = _cartItemList.value
        updateSharedPref()


    }
    fun decreaseQuantity(cartItem: CartItem){
        if (_cartItemList.value?.contains(cartItem) == true) {
            val existingCartItem = _cartItemList.value?.find { it == cartItem }
            existingCartItem?.let {
                it.quantity -= 1
            }
        }
    }

    fun updateSharedPref() {
        _cartItemList.value = _cartItemList.value
        var quantityCount = _cartItemList.value?.sumOf { it.quantity }
        val cartJson = Gson().toJson(_cartItemList.value)
        editor.putString(CART, cartJson)
        editor.putInt(CART_COUNT, quantityCount ?: 0)
        editor.apply()
        cartChangeListener?.onCartChanged(getCartCount())
    }

    fun clearCart(){
        _cartItemList.value?.clear()
        updateSharedPref()
    }


    fun setCartChangeListener(listener: CartChangeListener) {
        cartChangeListener = listener
    }


}
