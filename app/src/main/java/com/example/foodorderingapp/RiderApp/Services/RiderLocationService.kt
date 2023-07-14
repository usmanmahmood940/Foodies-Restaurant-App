package com.example.foodorderingapp.RiderApp.Services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import com.example.foodorderingapp.Repositories.OrderRepository
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.NOTIFICATION_ID
import com.example.foodorderingapp.models.DeliveryInfo
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RiderLocationService() : Service() {
    private lateinit var locationClient: FusedLocationProviderClient
    @Inject
    lateinit var orderRepository: OrderRepository
    @Inject
    lateinit var sharedPreferences:SharedPreferences


    override fun onCreate() {
        super.onCreate()
        locationClient = LocationServices.getFusedLocationProviderClient(this)
       
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
        return START_STICKY
    }
    private fun createNotification(): Notification {
        val channelId = "YOUR_CHANNEL_ID"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Service Running")
            .setSmallIcon(com.example.foodorderingapp.R.drawable.logo)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return notificationBuilder.build()
    }


    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000)
            .setMinUpdateIntervalMillis(10000)
            .build()



        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            location?.let {
                saveLocationToFirebase(location.latitude, location.longitude)
            }

        }
    }

    private fun saveLocationToFirebase(latitude: Double, longitude: Double) {
        val orderId = sharedPreferences.getString(Constants.RIDER_RUNNING_ORDER,null)
        orderId?.let {
            orderRepository.updateRiderLocation(orderId, DeliveryInfo(latitude, longitude))
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
