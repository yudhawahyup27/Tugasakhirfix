package com.nairobi.absensi.dashboard.karyawan

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
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.StatusCard
import com.nairobi.absensi.model.AttendanceViewModel
import com.nairobi.absensi.utils.datePicker
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.getHourMinuteDistance
import com.nairobi.absensi.utils.isSameDay
import java.util.Date

@Composable
fun Attendance(navController: NavController, attendanceViewModel: AttendanceViewModel) {
    attendanceViewModel.refreshAttendancesWhere("userId", attendanceViewModel.user.value!!.id)

    val context = LocalContext.current

    if (attendanceViewModel.loading.value) {
        ProgressScreen()
    } else if (attendanceViewModel.error.value != null) {
        ErrorScreen(attendanceViewModel.error.value!!)
    } else {
        var filter by remember { mutableStateOf<Date?>(null) }

        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AppBar(navController, "Absensi")
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
                attendanceViewModel.attendances.value.sortedBy { it.date }
                    .reversed()
                    .filter {
                        filter == null || isSameDay(it.date, filter!!)
                    }
                    .forEach { attendance ->
                        val (color, title) = when (attendance.status) {
                            "CHECKIN" -> Pair(Color.Green, "Sudah Check In")
                            "CHECKOUT" -> Pair(Color.Green, "Masuk")
                            "LEAVE" -> Pair(Purple, "Cuti")
                            else -> Pair(Color.Red, "Tidak Masuk")
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
                                    formatDate(attendance.date, "EEEE, dd MMMM yyyy"),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                if (attendance.status != "ABSENT" && attendance.status != "LEAVE") {
                                    val officeOpen = attendanceViewModel.office.value!!.openTime
                                    val now = Date()
                                    now.hours = officeOpen.hours
                                    now.minutes = officeOpen.minutes
                                    Text(
                                        "Masuk: ${formatDate(attendance.checkIn, "HH:mm")}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    Text(
                                        "Telat: ${
                                            getHourMinuteDistance(
                                                attendance.checkIn,
                                                now
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                if (attendance.status == "CHECKOUT") {
                                    Text(
                                        "Keluar: ${formatDate(attendance.checkOut, "HH:mm")}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    Text(
                                        "Durasi: ${
                                            getHourMinuteDistance(
                                                attendance.checkOut,
                                                attendance.checkIn
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}