package com.nairobi.absensi.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
fun getCurrentLocation(activity: Activity, onFinish: (Double, Double) -> Unit) {
    val fusedLocationProviderClient =
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(activity)
    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
        if (it != null) {
            onFinish(it.latitude, it.longitude)
        } else {
            onFinish(0.0, 0.0)
        }
    }.addOnFailureListener {
        onFinish(0.0, 0.0)
    }
}

@SuppressLint("MissingPermission")
suspend fun getCurrentLocationSuspend(activity: Activity): Pair<Double, Double> {
    val fusedLocationProviderClient =
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(activity)
    return try {
        val location = fusedLocationProviderClient.lastLocation.await()
        Pair(location.latitude, location.longitude)
    } catch (e: Exception) {
        Pair(0.0, 0.0)
    }
}

fun getAddress(
    activity: Activity,
    latitude: Double,
    longitude: Double,
    onFinish: (String) -> Unit
) {
    val geocoder = android.location.Geocoder(activity, java.util.Locale.getDefault())
    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
    if (!addresses.isNullOrEmpty()) {
        val address = addresses[0]
        val addressLine = address.getAddressLine(0)
        onFinish(addressLine)
    } else {
        onFinish("")
    }
}

fun getLatLng(activity: Activity, address: String, onFinish: (Double, Double) -> Unit) {
    val geocoder = android.location.Geocoder(activity, java.util.Locale.getDefault())
    val addresses = geocoder.getFromLocationName(address, 1)
    if (!addresses.isNullOrEmpty()) {
        val address = addresses[0]
        onFinish(address.latitude, address.longitude)
    } else {
        onFinish(0.0, 0.0)
    }
}

fun getDistance(
    lat0: Double,
    lng0: Double,
    lat1: Double,
    lng1: Double,
): Float {
    val loc0 = Location("")
    loc0.latitude = lat0
    loc0.longitude = lng0
    val loc1 = Location("")
    loc1.latitude = lat1
    loc1.longitude = lng1
    return loc0.distanceTo(loc1)
}