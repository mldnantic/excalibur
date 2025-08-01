package com.example.mobilnibus.location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.mobilnibus.R
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("DEPRECATION")
class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob()+ Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start()
    {
        val notification = NotificationCompat.Builder(this,"location")
            .setContentTitle("Tracking...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_location_service)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(5000L)
            .catch { e->
                e.printStackTrace()
            }
            .onEach { location ->
                val lat = location.latitude.toString()
                val lng = location.longitude.toString()

                latitude = location.latitude
                longitude = location.longitude
                isActive=true

                //Create/update bus marker
                if (activeMarker) {
                    val busData = mapOf(
                        "lat" to latitude,
                        "lng" to longitude,
                    )
                    firestore.collection("busmarkers")
                        .document(busId)
                        .update(busData)
                }

                val updatedNotification = notification.setContentText(
                    "Location: ($lat,$lng)"
                )
                notificationManager.notify(1,updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1,notification.build())
    }
    private fun stop()
    {
        isActive=false
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var latitude:Double=0.0
        var longitude:Double=0.0
        var isActive:Boolean=false
        var busId:String = ""
        var linija:String = ""
        var activeMarker:Boolean=false
    }

}