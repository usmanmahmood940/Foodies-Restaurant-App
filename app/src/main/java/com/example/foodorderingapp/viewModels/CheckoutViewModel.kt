package com.example.foodorderingapp.viewModels

import android.content.SharedPreferences
import android.location.Location
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.CartManager
import com.example.foodorderingapp.Repositories.OrderRepository
import com.example.foodorderingapp.Utils.Constants.RUNNING_ORDER_ID
import com.example.foodorderingapp.Utils.Constants.VALID_DISTANCE
import com.example.foodorderingapp.Utils.Constants.ZERO_DOUBLE
import com.example.foodorderingapp.models.Order
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartManager: CartManager,
    private val sharedPreferences: SharedPreferences,
    private val auth:FirebaseAuth
) : ViewModel() {
    var latitude: Double = ZERO_DOUBLE
    var longitude: Double = ZERO_DOUBLE
    var distance: Double? = null

    val errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val successMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    fun validDistance(): Boolean {
        distance?.let { it ->
            return it <= VALID_DISTANCE
        }
        return false
    }


    fun calculateDistanceInKm(firstLatLng: LatLng, secondLatLng: LatLng): Double {

        val startLocation = Location("StartLocation")
        startLocation.latitude = firstLatLng.latitude
        startLocation.longitude = firstLatLng.longitude

        val endLocation = Location("EndLocation")
        endLocation.latitude = secondLatLng.latitude
        endLocation.longitude = secondLatLng.longitude

        val distance = startLocation.distanceTo(endLocation)

        val distanceInKilometers = distance / 1000.0
        return distanceInKilometers
    }

    fun placeOrder(order: Order,callback: (Boolean, Exception?) -> Unit) {
        orderRepository.createOrder(order) { success, exception,orderId ->
            if (success) {
                cartManager.clearCart()
                sharedPreferences.edit{
                   putString(RUNNING_ORDER_ID,orderId)
                   apply()
                }
                callback(success,null)
            } else {
                errorMessage.value = exception?.message
                callback(success,exception)
            }

        }


    }
}