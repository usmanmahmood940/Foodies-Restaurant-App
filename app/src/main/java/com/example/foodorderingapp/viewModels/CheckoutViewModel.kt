package com.example.foodorderingapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.Repositories.OrderRepository
import com.example.foodorderingapp.models.Order
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var distance: Double? = null

    val errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val successMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    fun validDistance(): Boolean {
        distance?.let { it ->
            return it <= 5
        }
        return false
    }

    fun calculateDistanceInKm(firstLatLng: LatLng, secondLatLng: LatLng): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers
        val dLat = Math.toRadians(secondLatLng.latitude - firstLatLng.latitude)
        val dLon = Math.toRadians(secondLatLng.longitude - firstLatLng.longitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(firstLatLng.latitude)) * Math.cos(
            Math.toRadians(
                secondLatLng.longitude
            )
        ) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = earthRadius * c
        return Math.abs(distance)
    }

    fun placeOrder(order: Order) {
        orderRepository.createOrder(order) { success, exception ->
            if (success) {
                successMessage.value= "Order Placed"
            } else {
             errorMessage.value = exception?.message
            }

        }


    }
}