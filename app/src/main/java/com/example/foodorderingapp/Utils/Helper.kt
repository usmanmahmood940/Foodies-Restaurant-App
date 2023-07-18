package com.example.foodorderingapp.Utils

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface.OnDismissListener
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.text.InputType
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.foodorderingapp.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ui.IconGenerator
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

object Helper {

    fun showAlertDialog(
        context: WeakReference<Context>,
        title: String,
        message: String?,
        onDismissListener: OnDismissListener? = null,
    ) {
        val contextRef = context.get()
        if (contextRef != null) {
            val alertDialog = AlertDialog.Builder(contextRef).apply {
                setTitle(title)
                setMessage(message)
                setOnDismissListener(onDismissListener)
                show()
            }
        }
    }

    fun generateRandomStringWithTime(): String {
        val timestamp = System.currentTimeMillis()
        val randomString = UUID.randomUUID().toString()
        return "$randomString-$timestamp"
    }

    fun String.isValidEmail(): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return Regex(emailRegex).matches(this)
    }

    fun EditText.showError(errorMsg: String) {
        error = errorMsg
        requestFocus()
    }

    fun EditText.togglePasswordVisibility() {
        val inputType = inputType
        val newInputType =
            if (inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        setInputType(newInputType)
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

    fun Bitmap.toUri(context: Context): Uri? {
        // Create a file to save the bitmap
        val file = File(context.cacheDir, "profileImage.jpg")
        try {
            val outputStream = FileOutputStream(file)
            compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Generate a content URI for the file using FileProvider
            return FileProvider.getUriForFile(context, "com.example.fileprovider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun Bitmap.getImprovedBitmap(): Bitmap {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


}