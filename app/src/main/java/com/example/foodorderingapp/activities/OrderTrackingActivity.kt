package com.example.foodorderingapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants.FULL_SCREEN_OPACITY
import com.example.foodorderingapp.Utils.Constants.HALF_SCREEN_OPACITY
import com.example.foodorderingapp.Utils.Constants.MAP_ZOOM_HEIGHT
import com.example.foodorderingapp.Utils.Constants.MAP_ZOOM_WIDTH
import com.example.foodorderingapp.Utils.Constants.MY_LOCATION
import com.example.foodorderingapp.Utils.Constants.ORDER_ASSIGNING
import com.example.foodorderingapp.Utils.Constants.ORDER_DELIVERED
import com.example.foodorderingapp.Utils.Constants.RUNNING_ORDER_ID
import com.example.foodorderingapp.Utils.Constants.ORDER_IN_DELIVERY
import com.example.foodorderingapp.Utils.Constants.ORDER_PROCEED
import com.example.foodorderingapp.Utils.Constants.PROGRESS_50
import com.example.foodorderingapp.Utils.Constants.PROGRESS_85
import com.example.foodorderingapp.Utils.Constants.RIDER_LOCATION
import com.example.foodorderingapp.Utils.Constants.RUNNING_ORDER
import com.example.foodorderingapp.Utils.Constants.ZERO
import com.example.foodorderingapp.Utils.Helper.getCustomMapIcon
import com.example.foodorderingapp.Utils.Helper.showAlertDialog
import com.example.foodorderingapp.databinding.ActivityOrderTrackingBinding
import com.example.foodorderingapp.models.DeliveryInfo
import com.example.foodorderingapp.models.Order
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
import java.lang.ref.WeakReference
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

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_order_tracking) as SupportMapFragment
        mapFragment.getMapAsync(this)

        orderTrackingViewModel.runningOrder.observe(this) { response ->
            when (response) {
                is CustomResponse.Loading -> {
                    binding.apply {
                        progressBarLoading.visibility = View.VISIBLE
                        clOrderTracking.alpha = 0.1f
                    }
                }
                is CustomResponse.Success -> {
                    binding.apply {
                        progressBarLoading.visibility = View.GONE
                        clOrderTracking.alpha = FULL_SCREEN_OPACITY
                    }
                    handleSuccessResponse(response.data)
                }
                is CustomResponse.Error -> {
                    binding.apply {
                        progressBarLoading.visibility = View.GONE
                        clOrderTracking.alpha = FULL_SCREEN_OPACITY
                    }
                    handleError(response.errorMessage.toString())
                }
                else -> {}
            }
        }

        sharedPreferences.getString(RUNNING_ORDER_ID, null)?.let { orderId ->
            orderTrackingViewModel.startObservingOrder(orderId)
        }
    }

    private fun handleSuccessResponse(data: Order?) {
        data?.orderTracking?.apply {
            when (status) {
                ORDER_PROCEED -> {
                    handleOrderProceedStatus()
                }
                ORDER_IN_DELIVERY -> {
                    deliveryInfo?.let { deliveryInfo ->
                        handleOrderInDeliverStatus(deliveryInfo,data.customerDeliveryInfo)
                    }
                }
                ORDER_DELIVERED -> {
                    handleOrderDeliveredStatus()
                }
                else -> {}
            }
        }
    }



    private fun handleOrderProceedStatus() {
        with(binding) {
            ivGifCooking.visibility = View.VISIBLE
            rlMap.visibility = View.GONE
            pbTracking.progress = PROGRESS_50
            tvOrderProceedCircle.background = getDrawable(R.drawable.shape_circle_btn_orange)
            tvOrderProceed.text = ORDER_PROCEED
            tvTrackingLabel.text = ORDER_ASSIGNING
        }
    }

    private fun handleOrderInDeliverStatus(deliveryInfo:DeliveryInfo,customerDeliveryInfo: DeliveryInfo) {
        with(binding) {
            ivGifCooking.visibility = View.GONE
            rlMap.visibility = View.VISIBLE
            pbTracking.progress = PROGRESS_85
            tvOrderProceedCircle.background = getDrawable(R.drawable.shape_circle_btn_orange)
            tvDileveryCircle.background = getDrawable(R.drawable.shape_circle_btn_orange)
            tvDilevery.text = ORDER_IN_DELIVERY
            tvTrackingLabel.text = ORDER_IN_DELIVERY
            deliveryInfo?.let { deliveryInfo ->
                setupMapLatLng(deliveryInfo, customerDeliveryInfo)
            }
        }
    }

    private fun handleOrderDeliveredStatus(){
        sharedPreferences.edit {
            putString(RUNNING_ORDER_ID, null)
            putBoolean(RUNNING_ORDER, false)
            apply()
        }
        showAlertDialog(
            WeakReference(this@OrderTrackingActivity),
            getString(R.string.information),
            getString(R.string.order_delivered_message),
            onDismissListener = object : OnDismissListener {
                override fun onDismiss(dialog: DialogInterface?) {
                    finish()
                }
            }
        )
    }


    private fun handleError(errorMessage: String) {
        showAlertDialog(
            WeakReference(this@OrderTrackingActivity),
            getString(R.string.error),
            errorMessage
        )
    }

    private fun setupMapLatLng(
        riderDeliveryInfo: DeliveryInfo,
        customerDeliveryInfo: DeliveryInfo,
    ) {
        val riderLatLng =
            LatLng(riderDeliveryInfo.locationLatitude, riderDeliveryInfo.locationLongitude)
        if (riderMarker == null) {
            val customerLatLng = LatLng(
                customerDeliveryInfo.locationLatitude,
                customerDeliveryInfo.locationLongitude
            )

            googleMap.addMarker(
                MarkerOptions()
                    .position(customerLatLng)
                    .title(MY_LOCATION)
                    .icon(getCustomMapIcon(applicationContext, R.drawable.ic_home))
            )

            riderMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(riderLatLng)
                    .title(RIDER_LOCATION)
                    .icon(getCustomMapIcon(applicationContext, R.drawable.ic_delivery_bike_24))
            )

            val boundsBuilder = LatLngBounds.Builder()
            boundsBuilder.include(customerLatLng!!)
            boundsBuilder.include(riderLatLng)
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    MAP_ZOOM_WIDTH,
                    MAP_ZOOM_HEIGHT,
                    ZERO
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
