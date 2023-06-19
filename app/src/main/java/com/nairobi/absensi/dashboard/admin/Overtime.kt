package com.nairobi.absensi.dashboard.admin

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.navigation.NavController
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.FabAdd
import com.nairobi.absensi.components.Orange
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.SearchDateText
import com.nairobi.absensi.components.StatusCard
import com.nairobi.absensi.model.OvertimeViewModel
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.getHourMinuteDistance
import com.nairobi.absensi.utils.isSameDay
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Overtime(navController: NavController, overtimeViewModel: OvertimeViewModel) {
    overtimeViewModel.refreshOvertimes()

    BackHandler {
        navController.popBackStack()
    }
    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route != "overtime/add" && dest.route != "overtime") {
            overtimeViewModel.clean()
        }
    }

    if (overtimeViewModel.loading.value) {
        ProgressScreen()
    } else if (overtimeViewModel.error.value != null) {
        ErrorScreen(overtimeViewModel.error.value!!)
    } else {
        val overtimes = overtimeViewModel.overtimes.value
        var date by remember { mutableStateOf<Date?>(null) }
        var text by remember { mutableStateOf("") }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
            ) {
                AppBar(navController, "Lembur")
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
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    overtimes.sortedBy { it.startTime }
                        .reversed()
                        .filter {
                            date?.let { date ->
                                isSameDay(it.startTime, date) || isSameDay(it.endTime, date)
                            } ?: true
                        }
                        .filter {
                            val overtimeUser = overtimeViewModel.findOvertimeUser(it)
                            if (text.isNotEmpty()) {
                                it.status.lowercase().contains(text.lowercase()) ||
                                        overtimeUser?.let { user ->
                                            user.name.lowercase().contains(text.lowercase()) ||
                                                    user.email.lowercase()
                                                        .contains(text.lowercase())
                                        } ?: true
                            } else {
                                true
                            }
                        }
                        .forEach { overtime ->
                            val user = overtimeViewModel.findOvertimeUser(overtime)
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
                                    if (overtime.status == "APPROVED") {
                                        Text(
                                            "Mulai: ${
                                                formatDate(
                                                    overtime.startTime,
                                                    "dd MMMM yyyy HH:mm"
                                                )
                                            }",
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Text(
                                            "Selesai: ${
                                                formatDate(
                                                    overtime.endTime,
                                                    "dd MMMM yyyy HH:mm"
                                                )
                                            }",
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Text(
                                            "Durasi: ${
                                                getHourMinuteDistance(
                                                    overtime.endTime,
                                                    overtime.startTime
                                                )
                                            }",
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    } else {
                                        Text(formatDate(overtime.startTime, "dd MMMM yyyy HH:mm"))
                                    }
                                    Text(
                                        "${overtime.reason}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.LightGray, MaterialTheme.shapes.small)
                                            .padding(8.dp),
                                    )
                                }
                            }
                        }
                }
            }
            FabAdd {
                navController.navigate("overtime/add")
            }
        }
    }
}