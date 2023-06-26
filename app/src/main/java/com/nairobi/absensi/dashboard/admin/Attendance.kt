package com.nairobi.absensi.dashboard.admin

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.SearchDateText
import com.nairobi.absensi.components.StatusCard
import com.nairobi.absensi.model.AttendanceViewModel
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.getHourMinuteDistance
import com.nairobi.absensi.utils.isSameDay
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Attendance(navController: NavController, attendanceViewModel: AttendanceViewModel) {
    attendanceViewModel.refreshAttendances()

    BackHandler {
        navController.popBackStack()
    }
    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route != "attendance") {
            attendanceViewModel.clean()
        }
    }

    if (attendanceViewModel.loading.value) {
        ProgressScreen()
    } else if (attendanceViewModel.error.value != null) {
        ErrorScreen(attendanceViewModel.error.value!!)
    } else {
        val attendances by remember { mutableStateOf(attendanceViewModel.attendances.value) }
        var date by remember { mutableStateOf<Date?>(null) }
        var text by remember { mutableStateOf<String>("") }

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
                SearchDateText(
                    date = date,
                    onDateChange = { date = it },
                    text = text,
                    onTextChange = { text = it }
                )
            }
            val leaveCount = attendances.filter { it.status == "LEAVE" }.size
            val notWork =
                attendances.filter { it.status != "CHECKIN" && it.status != "CHECKOUT" && it.status != "LEAVE" }.size
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Cuti: $leaveCount",
                    fontSize = 18.sp
                )
                Text(
                    text = "Bolos: $notWork",
                    fontSize = 18.sp
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                attendances.sortedBy { it.date }
                    .reversed()
                    .filter {
                        date?.let { date -> isSameDay(it.date, date) } ?: true
                    }
                    .filter {
                        if (text.isNotEmpty()) {
                            val attendanceUser = attendanceViewModel.findAttendanceUser(it)
                            attendanceUser?.let { user ->
                                user.name.lowercase(Locale.getDefault())
                                    .contains(text.lowercase(Locale.getDefault())) ||
                                        user.email.lowercase(Locale.getDefault())
                                            .contains(text.lowercase(Locale.getDefault()))
                            } ?: true
                        } else {
                            true
                        }
                    }
                    .forEach { attendance ->
                        val user = attendanceViewModel.findAttendanceUser(attendance)
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
                                user?.let {
                                    Text(
                                        it.name,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        it.email,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
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