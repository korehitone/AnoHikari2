package com.syntxr.anohikari3.service.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

class LocationClientImpl(
    private val application: Application,
    private val externalScope: CoroutineScope,
    private val locationClient : FusedLocationProviderClient
) : LocationClient {
    override fun requestLocationUpdate(): Flow<LocationClientTracker<Location?>> =
        callbackFlow<LocationClientTracker<Location?>> {

            val locationRequest = LocationRequest.Builder(1000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            val locationPermission = ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val locationManager = application.getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager


            val gpsPermission = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (!locationPermission){
                trySend(LocationClientTracker.MissingPermission())
                close()
                return@callbackFlow
            }

            if(!gpsPermission){
                trySend(LocationClientTracker.NoGps())
                close()
                return@callbackFlow
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    trySend(LocationClientTracker.Success(result.lastLocation))
                }
            }

            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnFailureListener { exception ->
                trySend(LocationClientTracker.Error())
                close(exception)
            }

            awaitClose {
                locationClient.removeLocationUpdates(locationCallback)
            }
    }.shareIn(
        externalScope,
        replay = 0,
        started = SharingStarted.WhileSubscribed()
    )
}