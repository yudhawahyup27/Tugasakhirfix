package com.nairobi.absensi.dashboard.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.model.AttendanceViewModel
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.isToday
import java.util.Date

@Composable
fun Early(navController: NavController, attendanceViewModel: AttendanceViewModel) {
    attendanceViewModel.prepareCheckInCheckOut()
    LaunchedEffect("early") {
        attendanceViewModel.getEarlyCheckouts()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBar(navController, "Cuti")
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val pendings = attendanceViewModel.earlyCheckouts.value.filter {
                it.status == "PENDING" && isToday(it.date)
            }
            pendings.forEach { early ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = attendanceViewModel.user.value!!.name,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = formatDate(early.date, "HH:mm"),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val checkin =
                                        attendanceViewModel.attendances.value.find { isToday(it.date) }
                                    if (checkin != null) {
                                        checkin.status = "CHECKOUT"
                                        checkin.checkOut = Date()
                                        attendanceViewModel.updateAttendance(checkin)
                                    }
                                    early.status = "APPROVED"
                                    attendanceViewModel.updateEarlyCheckout(early)
                                    attendanceViewModel.getEarlyCheckouts()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Text(text = "Setujui")
                            }
                            Button(
                                onClick = {
                                    early.status = "REJECTED"
                                    attendanceViewModel.updateEarlyCheckout(early)
                                    attendanceViewModel.getEarlyCheckouts()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Text(text = "Tolak")
                            }
                        }
                    }
                }
            }
        }
    }
}