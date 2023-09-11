package com.example.foodorderingapp.RiderApp


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.RiderApp.Services.RiderLocationService
import com.example.foodorderingapp.RiderApp.ViewModels.RiderMapViewModel
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.MAP_ZOOM_HEIGHT
import com.example.foodorderingapp.Utils.Constants.MAP_ZOOM_WIDTH
import com.example.foodorderingapp.Utils.Constants.ORDER_DELIVERED
import com.example.foodorderingapp.Utils.Constants.PADDING
import com.example.foodorderingapp.Utils.Helper.getCustomMapIcon
import com.example.foodorderingapp.Utils.Helper.googleMapUri
import com.example.foodorderingapp.Utils.Helper.showAlertDialog
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
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class RiderMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityRiderMapBinding
    private lateinit var riderMapViewModel: RiderMapViewModel
    private lateinit var riderLatLng: LatLng
    private var customerLatLng: LatLng? = null
    private lateinit var boundsBuilder: LatLngBounds.Builder
    private var riderMarker: Marker? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

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

                val intentUri = googleMapUri(customerLatLng!!, riderMarker!!.position)
                val intent = Intent(Intent.ACTION_VIEW, intentUri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)

            } else {
                showAlertDialog(
                    WeakReference(this@RiderMapActivity),
                    getString(R.string.information),
                    getString(R.string.wait)
                )
            }

        }
        binding.btnOrderCompleted.setOnClickListener {
            val orderId = riderMapViewModel.checkRunningOrder()
            riderMapViewModel.updateOrderStatus(orderId = orderId!!, orderStatus = ORDER_DELIVERED)
            { success, exception ->
                if (success) {
                    riderMapViewModel.stopTrackingOrder(orderId)
                    val serviceIntent = Intent(this, RiderLocationService::class.java)
                    stopService(serviceIntent)
                    startActivity(
                        Intent(this@RiderMapActivity, RiderHomeActivity::class.java)
                    )
                    sharedPreferences.edit {
                        putString(Constants.RIDER_RUNNING_ORDER,null)
                        apply()
                    }

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
                                .title(getString(R.string.customer_location))
                                .icon(
                                    getCustomMapIcon(
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
                                    .title(getString(R.string.your_location))
                                    .icon(
                                        getCustomMapIcon(
                                            applicationContext,
                                            R.drawable.ic_delivery_bike_24
                                        )
                                    )
                            )
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    boundsBuilder.build(),
                                    MAP_ZOOM_WIDTH,
                                    MAP_ZOOM_HEIGHT,
                                    PADDING
                                )
                            )
                        } else {
                            riderMarker?.position = riderLatLng
                        }


                    }
                }
                is CustomResponse.Error -> {
                    showAlertDialog(
                        WeakReference(this@RiderMapActivity),
                        getString(R.string.error),
                        it.errorMessage
                    )
                }
                else -> {}
            }
        }
    }

}