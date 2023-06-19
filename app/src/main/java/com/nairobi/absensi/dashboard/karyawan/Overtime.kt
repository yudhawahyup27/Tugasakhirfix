package com.nairobi.absensi.dashboard.karyawan

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.FormText
import com.nairobi.absensi.components.Orange
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.StatusCard
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.model.OvertimeViewModel
import com.nairobi.absensi.utils.datePicker
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.getCurrentLocation
import com.nairobi.absensi.utils.getDistance
import com.nairobi.absensi.utils.isSameDay
import java.util.Date

@Composable
fun Overtime(navController: NavController, overtimeViewModel: OvertimeViewModel) {
    overtimeViewModel.refreshOvertimesWhere("userId", overtimeViewModel.user.value!!.id)

    val context = LocalContext.current

    fun approveOvertime(overtime: com.nairobi.absensi.data.Overtime) {
        val loading = loadingAlert(context)
        getCurrentLocation(context as Activity) { lat, lng ->
            val officeAddress = overtimeViewModel.office.value!!.address
            val lat1 = officeAddress.latitude
            val lng1 = officeAddress.longitude
            val distance = getDistance(lat, lng, lat1, lng1)
            if (distance < 500) {
                overtime.status = "APPROVED"
                overtimeViewModel.updateOvertime(overtime) {
                    loading.dismissWithAnimation()
                }
            } else {
                errorAlert(
                    context,
                    "Gagal",
                    "Kamu harus berada di kantor untuk melakukan absensi lembur."
                )
            }
        }
    }

    if (overtimeViewModel.loading.value) {
        ProgressScreen()
    } else if (overtimeViewModel.error.value != null) {
        ErrorScreen(overtimeViewModel.error.value!!)
    } else {
        var filter by remember { mutableStateOf<Date?>(null) }

        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AppBar(navController, "Lembur")
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val overtime = overtimeViewModel.overtimes.value
                    .filter { it.status == "PENDING" }.maxByOrNull { it.startTime }
                overtime?.let {
                    val enabled = isSameDay(it.startTime, Date()) && it.startTime.before(Date())
                            && it.endTime.after(Date())
                    Text(
                        "Kamu memiliki lembur untuk tanggal ${
                            formatDate(
                                it.startTime,
                                "dd MMMM yyyy HH:mm"
                            )
                        }, silahkan lakukan absensi lembur saat waktu lembur tiba.",
                    )
                    Button(
                        onClick = {
                            if (enabled) {
                                approveOvertime(it)
                            }
                        },
                        colors = if (enabled) {
                            ButtonDefaults.textButtonColors(
                                containerColor = Purple,
                                contentColor = Color.White,
                            )
                        } else ButtonDefaults.textButtonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.White,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    ) {
                        Text("Absen Lembur")
                    }
                }
                FormText(
                    text = filter?.let { formatDate(it, "dd MMMM yyyy") } ?: "",
                    label = "Filter",
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    disabled = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                filter = null
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePicker(context) {
                                filter = it
                            }
                        }
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                overtimeViewModel.overtimes.value.sortedBy { it.startTime }
                    .reversed()
                    .filter {
                        filter == null || isSameDay(it.startTime, filter!!) || isSameDay(
                            it.endTime,
                            filter!!
                        )
                    }
                    .forEach { overtime ->
                        val (color, title) = when (overtime.status) {
                            "PENDING" -> Pair(Orange, "Menunggu")
                            "APPROVED" -> Pair(Color.Green, "Dikerjakan")
                            else -> Pair(Color.Red, "Ditolak")
                        }
                        StatusCard(
                            title = title,
                            color = color,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Text(
                                    "Mulai: ${
                                        formatDate(
                                            overtime.startTime,
                                            "dd MMMM yyyy HH:mm"
                                        )
                                    }",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Selesai: ${
                                        formatDate(
                                            overtime.endTime,
                                            "dd MMMM yyyy HH:mm"
                                        )
                                    }",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    overtime.reason,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray, MaterialTheme.shapes.small)
                                        .padding(4.dp),
                                )
                            }
                        }
                    }
            }
        }
    }
}