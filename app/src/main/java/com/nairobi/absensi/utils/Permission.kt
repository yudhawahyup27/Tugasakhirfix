package com.nairobi.absensi.utils

import android.app.Activity

fun checkLocationPermission(activity: Activity): Boolean {
    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    var isGranted = true
    for (permission in permissions) {
        val permissionCheck = activity.checkSelfPermission(permission)
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_DENIED) {
            isGranted = false
            break
        }
    }
    return isGranted
}

fun checkAndRequestPermission(activity: Activity, onComplete: (Boolean) -> Unit) {
    val permissions: Array<String> = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    val permissionDeniedList = arrayListOf<String>()
    for (permission in permissions) {
        val permissionCheck = activity.checkSelfPermission(permission)
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_DENIED) {
            permissionDeniedList.add(permission)
        }
    }
    if (permissionDeniedList.isNotEmpty()) {
        val permissionArray = permissionDeniedList.toTypedArray()
        activity.requestPermissions(permissionArray, 1)
        onComplete(false)
    } else {
        onComplete(true)
    }
}