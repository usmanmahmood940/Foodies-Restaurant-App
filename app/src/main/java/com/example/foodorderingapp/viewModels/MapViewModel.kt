package com.example.foodorderingapp.viewModels

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    lateinit var pinLocation : LatLng
}