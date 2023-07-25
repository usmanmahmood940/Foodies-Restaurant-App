package com.example.foodorderingapp.Utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.content.DialogInterface.OnShowListener
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.example.foodorderingapp.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ui.IconGenerator
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*


object Helper {

    fun showAlertDialog(
        context: WeakReference<Context>,
        title: String,
        message: String?,
        onDismissListener: OnDismissListener? = null,
        positiveListener: DialogInterface.OnClickListener? = null,
        positiveButtonText: String = "Ok",

        ) {
        val contextRef = context.get()
        if (contextRef != null) {
            val alertDialog = AlertDialog.Builder(contextRef, R.style.CustomDialog).apply {
                setTitle(title)
                setMessage(message)
                setOnDismissListener(onDismissListener)
                if (positiveListener != null) {
                    setPositiveButton(positiveButtonText, positiveListener)
                }


            }.create()
            alertDialog.setOnShowListener(OnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).apply{
                    setTextColor(ContextCompat.getColor(contextRef, R.color.orange))
                }
            })
            alertDialog.show()
        }
    }

    fun generateRandomStringWithTime(): String {
        val timestamp = System.currentTimeMillis()
        val randomString = UUID.randomUUID().toString()
        return "$randomString-$timestamp"
    }


    fun getAddressFromLocation(geocoder: Geocoder, latitude: Double, longitude: Double): String {
        var addressText = ""
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[Constants.ZERO]
                val sb = StringBuilder()

                for (i in 0 until address.maxAddressLineIndex + 1) {
                    sb.append(address.getAddressLine(i))
                    if (i < address.maxAddressLineIndex) {
                        sb.append(", ")
                    }
                }

                addressText = sb.toString()
            }
        } catch (e: IOException) {
        }

        return addressText
    }

    fun getCustomMapIcon(context: Context, icon: Int): BitmapDescriptor {
        val iconGenerator = IconGenerator(context)
        iconGenerator.setBackground(
            ContextCompat.getDrawable(
                context,
                icon
            )
        )
        return BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
    }

    fun googleMapUri(destination: LatLng, source: LatLng): Uri? {
        val destinationLatLng =
            "${destination.latitude},${destination.longitude}"
        val startingLatLng = "${source.latitude},${source.longitude}"
        val uri =
            "https://www.google.com/maps/dir/?api=1&destination=$destinationLatLng&origin=$startingLatLng"

        return Uri.parse(uri)
    }


}