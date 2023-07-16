package com.example.foodorderingapp.activities

import android.app.AlertDialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.ORDER_DELIVERED
import com.example.foodorderingapp.Utils.Constants.ORDER_ID
import com.example.foodorderingapp.Utils.Constants.ORDER_IN_DELIVERY
import com.example.foodorderingapp.Utils.Constants.ORDER_PROCEED
import com.example.foodorderingapp.Utils.Constants.RUNNING_ORDER
import com.example.foodorderingapp.Utils.Helper.getCustomMapIcon
import com.example.foodorderingapp.databinding.ActivityOrderTrackingBinding
import com.example.foodorderingapp.models.DeliveryInfo
import com.example.foodorderingapp.models.OrderTracking
import com.example.foodorderingapp.viewModels.OrderTrackingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderTrackingActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var binding: ActivityOrderTrackingBinding
    private lateinit var orderTrackingViewModel: OrderTrackingViewModel

    private lateinit var googleMap: GoogleMap
    private var riderMarker: Marker? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderTrackingViewModel = ViewModelProvider(this).get(OrderTrackingViewModel::class.java)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_order_tracking) as SupportMapFragment
        mapFragment.getMapAsync(this)

        orderTrackingViewModel.runningOrder.observe(this) {
            when (it) {
                is CustomResponse.Success -> {
                    when (it.data?.orderTracking?.status) {

                        ORDER_PROCEED -> {
                            binding.ivGifCooking.visibility = View.VISIBLE
                            binding.rlMap.visibility = View.GONE
                            binding.pbTracking.progress = 50
                            binding.tvOrderProceedCircle.background = ContextCompat.getDrawable(
                                this, R.drawable.shape_circle_btn_orange
                            )
                            binding.tvOrderProceed.text = ORDER_PROCEED
                            binding.tvTrackingLabel.text = "Order Assigning to Rider"

                        }
                        ORDER_IN_DELIVERY -> {
                            binding.ivGifCooking.visibility = View.GONE
                            binding.rlMap.visibility = View.VISIBLE
                            binding.pbTracking.progress = 85
                            binding.tvOrderProceedCircle.background = ContextCompat.getDrawable(
                                this, R.drawable.shape_circle_btn_orange
                            )
                            binding.tvDileveryCircle.background = ContextCompat.getDrawable(
                                this, R.drawable.shape_circle_btn_orange
                            )
                            binding.tvDilevery.text = ORDER_IN_DELIVERY
                            binding.tvTrackingLabel.text = ORDER_IN_DELIVERY

                            it.data.orderTracking.deliveryInfo?.apply {
                                setupMapLatLng(this,it.data.customerDeliveryInfo)
                            }
                        }
                        ORDER_DELIVERED -> {
                            sharedPreferences.edit {
                                putString(ORDER_ID, null)
                                putBoolean(RUNNING_ORDER,false)
                                apply()
                            }
                            val alertDialog = AlertDialog.Builder(this)
                            alertDialog.setTitle("Infromation")
                            alertDialog.setMessage("Enjoy your Orrder")
                            alertDialog.show()
                            finish()
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

        val orderId = sharedPreferences.getString(ORDER_ID, null)
        orderId?.let {
            orderTrackingViewModel.startObservingOrder(orderId)
//            val orderDelivery = OrderTracking()
//            orderDelivery.proceedOrder()
//
//            CoroutineScope(Dispatchers.IO).launch {
//                orderTrackingViewModel.updateOrderStatus(orderId, orderDelivery)
//            }

        }


    }

    private fun setupMapLatLng(riderDeliveryInfo: DeliveryInfo, customerDeliveryInfo: DeliveryInfo) {
        val riderLatLng = LatLng(riderDeliveryInfo.locationLatitude, riderDeliveryInfo.locationLongitude)
        if (riderMarker == null) {
            val customerLatLng = LatLng(
                customerDeliveryInfo.locationLatitude,
                customerDeliveryInfo.locationLongitude
            )
            googleMap.addMarker(
                MarkerOptions()
                    .position(customerLatLng)
                    .title("My Location")
                    .icon(getCustomMapIcon(applicationContext,R.drawable.ic_home))

            )
            riderMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(riderLatLng)
                    .title("Rider Location")
                    .icon(getCustomMapIcon(applicationContext,R.drawable.ic_delivery_bike_24))
            )
            val boundsBuilder = LatLngBounds.Builder()
            boundsBuilder.include(customerLatLng!!)
            boundsBuilder.include(riderLatLng)
            googleMap.moveCamera(
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

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

    }
}