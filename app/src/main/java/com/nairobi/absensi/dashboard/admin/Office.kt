package com.nairobi.absensi.dashboard.admin

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.GeoPoint
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.FormText
import com.nairobi.absensi.components.MapPickLauncher
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.components.successAlert
import com.nairobi.absensi.model.OfficeViewModel
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.getAddress
import com.nairobi.absensi.utils.timePicker

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Office(navController: NavController, officeViewModel: OfficeViewModel) {
    val context = LocalContext.current
    if (officeViewModel.office.value == null && officeViewModel.error.value == null) {
        officeViewModel.getOffice()
        ProgressScreen()
    } else if (officeViewModel.error.value != null) {
        ErrorScreen(officeViewModel.error.value!!)
    } else {
        val address = officeViewModel.office.value!!.address
        var addressString by remember { mutableStateOf("") }
        getAddress(context as Activity, address.latitude, address.longitude) { addr ->
            addressString = addr
        }
        OfficeForm(navController, officeViewModel, addressString)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OfficeForm(navController: NavController, officeViewModel: OfficeViewModel, addr: String) {
    val context = LocalContext.current

    val office = officeViewModel.office.value!!

    var startTime by remember { mutableStateOf(office.openTime) }
    var endTime by remember { mutableStateOf(office.closeTime) }
    var address by remember { mutableStateOf(office.address) }
    var addressString by remember { mutableStateOf(addr) }

    val mapPickLauncher = MapPickLauncher(context as Activity) { lat, long ->
        if (lat != 0.0 && long != 0.0) {
            address = GeoPoint(lat, long)
            getAddress(context, lat, long) { address ->
                addressString = address
            }
        }
    }

    val startTimeString = {
        formatDate(startTime, "HH:mm")
    }
    val endTimeString = {
        formatDate(endTime, "HH:mm")
    }

    val simpan = {
        val loading = loadingAlert(context)
        office.openTime = startTime
        office.closeTime = endTime
        office.address = address
        officeViewModel.setOffice(office) {
            loading.dismiss()
            successAlert(
                context,
                "Berhasil",
                "Data kantor berhasil disimpan"
            )
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AppBar(navController, "Kantor")
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                FormText(
                    text = startTimeString(),
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Date Range",
                        )
                    },
                    label = "Jam Buka",
                    disabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            timePicker(context) { date ->
                                startTime = date
                            }
                        }
                )
                FormText(
                    text = endTimeString(),
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Date Range",
                        )
                    },
                    label = "Jam Tutup",
                    disabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            timePicker(context) { date ->
                                endTime = date
                            }
                        }
                )
                FormText(
                    text = addressString,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Domain,
                            contentDescription = "Domain",
                        )
                    },
                    label = "Alamat Kantor",
                    disabled = true,
                    wrap = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val lat = address.latitude
                            val long = address.longitude
                            mapPickLauncher(lat, long)
                        }
                )
            }
        }
        Button(
            onClick = simpan,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.White,
                containerColor = Purple,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Simpan")
        }
    }
}