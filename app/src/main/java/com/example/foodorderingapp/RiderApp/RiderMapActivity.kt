package com.example.foodorderingapp.RiderApp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.RiderApp.Services.RiderLocationService
import com.example.foodorderingapp.RiderApp.ViewModels.RiderMapViewModel
import com.example.foodorderingapp.Utils.Constants.ORDER_DELIVERED
import com.example.foodorderingapp.Utils.Helper

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.foodorderingapp.databinding.ActivityRiderMapBinding
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

@AndroidEntryPoint
class RiderMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityRiderMapBinding
    private lateinit var riderMapViewModel: RiderMapViewModel
    private lateinit var riderLatLng: LatLng
    private var customerLatLng: LatLng? = null
    private lateinit var boundsBuilder: LatLngBounds.Builder
    private var riderMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityRiderMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        riderMapViewModel = ViewModelProvider(this).get(RiderMapViewModel::class.java)
        customerLatLng = riderMapViewModel.getCustomerLatLng()?.let {
            LatLng(it.locationLatitude, it.locationLongitude)
        }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        buttonListeners()


    }

    private fun buttonListeners() {
        binding.btnOpnGoogleMap.setOnClickListener {


            if (riderMarker != null && customerLatLng != null) {

                val destinationLatLng =
                    "${customerLatLng?.latitude},${customerLatLng?.longitude}"
                val startingLatLng = "${riderMarker!!.position.latitude},${riderMarker!!.position.longitude}"
                val intentUri =
                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destinationLatLng&origin=$startingLatLng")
                val intent = Intent(Intent.ACTION_VIEW, intentUri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)

            } else {
                val alertDialog = AlertDialog.Builder(this@RiderMapActivity)
                alertDialog.setTitle("Information")
                alertDialog.setMessage("Wait for a while")
                alertDialog.show()
            }


        }
        binding.btnOrderCompleted.setOnClickListener {
            riderMapViewModel.updateOrderStatus(
                orderId = riderMapViewModel.checkRunningOrder()!!,
                orderStatus = ORDER_DELIVERED
            ) { success, exception ->
                if (success) {
                    val serviceIntent = Intent(this, RiderLocationService::class.java)
                    stopService(serviceIntent)
                    startActivity(
                        Intent(this@RiderMapActivity, RiderHomeActivity::class.java)
                    )
                    finish()
                }
            }

        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        riderLocationListener()

    }

    private fun riderLocationListener() {

        riderMapViewModel.runningOrder.observe(this) {
            when (it) {
                is CustomResponse.Success -> {
                    it.data?.orderTracking?.deliveryInfo?.apply {

                        riderLatLng = LatLng(locationLatitude, locationLongitude)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(customerLatLng!!)
                                .title("Customer Location")
                                .icon(
                                    Helper.getCustomMapIcon(
                                        applicationContext,
                                        R.drawable.ic_location_pin
                                    )
                                )

                        )
                        boundsBuilder = LatLngBounds.Builder()
                        boundsBuilder.include(customerLatLng!!)
                        boundsBuilder.include(riderLatLng)
                        if (riderMarker == null) {
                            riderMarker = googleMap.addMarker(
                                MarkerOptions()
                                    .position(riderLatLng)
                                    .title("Your Location")
                                    .icon(
                                        Helper.getCustomMapIcon(
                                            applicationContext,
                                            R.drawable.ic_delivery_bike_24
                                        )
                                    )
                            )
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    boundsBuilder.build(),
                                    500,
                                    500,
                                    0
                                )
                            )
                        } else {
                            riderMarker?.position = riderLatLng
                        }


                    }
                }
                is CustomResponse.Error -> {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Error Message")
                    alertDialog.setMessage(it.errorMessage)
                    alertDialog.show()
                }
                else -> {}
            }
        }
    }
}