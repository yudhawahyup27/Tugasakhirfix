package com.nairobi.absensi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.Fab
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.utils.bitmapFromDrawableRes
import com.nairobi.absensi.utils.checkLocationPermission
import com.nairobi.absensi.utils.getCurrentLocation

class MapboxActivity : ComponentActivity() {
    private lateinit var mapView: MapView
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var pointAnnotationOptions: PointAnnotationOptions

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ResourceOptionsManager.getDefault(this, getString(R.string.mapbox_access_token))
        mapView = MapView(this, MapInitOptions(this, styleUri = Style.SATELLITE_STREETS))
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
        pointAnnotationOptions = PointAnnotationOptions()
        bitmapFromDrawableRes(this, R.drawable.red_marker)?.let {
            pointAnnotationOptions.withIconImage(it)
        }

        setCamera(latitude, longitude)
        setMarker(latitude, longitude)
        mapView.getMapboxMap().addOnMapClickListener { point ->
            latitude = point.latitude()
            longitude = point.longitude()
            setMarker(point.latitude(), point.longitude())
            true
        }

        showMap()

        val locPermission = checkLocationPermission(this)
        if (!locPermission) {
            errorAlert(
                this,
                "Tidak mempunyai izin lokasi",
                "Izin lokasi diperlukan untuk menentukan lokasi saat ini"
            )
        }

        val lat = intent.getDoubleExtra("latitude", 0.0)
        val lon = intent.getDoubleExtra("longitude", 0.0)
        if (lat != 0.0 && lon != 0.0) {
            latitude = lat
            longitude = lon
            setCamera(lat, lon)
            setMarker(lat, lon)
        } else if (locPermission) {
            getCurrentLocation(this) { lat, lng ->
                if (lat != 0.0 && lng != 0.0) {
                    latitude = lat
                    longitude = lng
                    setCamera(lat, lng)
                    setMarker(lat, lng)
                }
            }
        }
    }

    private fun showMap() {
        val context = this
        setContent {
            var satelite by remember { mutableStateOf(false) }
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                ) {
                    AppBar(
                        null,
                        "Pilih Lokasi",
                        {
                            Icon(Icons.Default.Save, contentDescription = null)
                        }
                    ) {
                        intent.putExtra("latitude", latitude)
                        intent.putExtra("longitude", longitude)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    AndroidView(
                        factory = { mapView }
                    )
                }
                Column {
                    Fab(
                        Icons.Default.Map
                    ) {
                        mapView.getMapboxMap().loadStyleUri(
                            if (satelite) Style.SATELLITE_STREETS else Style.MAPBOX_STREETS
                        )
                        satelite = !satelite
                    }
                    Fab(
                        Icons.Default.GpsFixed
                    ) {
                        getCurrentLocation(context) { lat, lng ->
                            if (lat != 0.0 && lng != 0.0) {
                                latitude = lat
                                longitude = lng
                                setCamera(lat, lng)
                                setMarker(lat, lng)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setCamera(lat: Double, lon: Double) {
        mapView.getMapboxMap().setCamera(
            cameraOptions {
                center(Point.fromLngLat(lon, lat))
                zoom(15.0)
            }
        )
    }

    private fun setMarker(lat: Double, lon: Double) {
        pointAnnotationManager.annotations.forEach {
            pointAnnotationManager.delete(it)
        }
        pointAnnotationManager.create(
            pointAnnotationOptions
                .withPoint(Point.fromLngLat(lon, lat))
        )
    }
}