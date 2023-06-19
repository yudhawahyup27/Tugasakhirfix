package com.nairobi.absensi.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.nairobi.absensi.MapboxActivity

@Composable
fun MapPickLauncher(
    activity: Activity,
    onResult: (Double, Double) -> Unit
): (Double, Double) -> Unit {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val lat = it.data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val lng = it.data?.getDoubleExtra("longitude", 0.0) ?: 0.0
            onResult(lat, lng)
        }
    return { lat, lng ->
        val intent = android.content.Intent(activity, MapboxActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lng)
        launcher.launch(intent)
    }
}