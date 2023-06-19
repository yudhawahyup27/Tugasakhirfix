package com.nairobi.absensi.dashboard.admin

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.Orange
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.SearchDateText
import com.nairobi.absensi.components.StatusCard
import com.nairobi.absensi.model.LeaveViewModel
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.isSameDay
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Leave(navController: NavController, leaveViewModel: LeaveViewModel) {
    leaveViewModel.refreshLeaves()

    BackHandler {
        navController.popBackStack()
    }
    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route != "leave/detail" && dest.route != "leave") {
            leaveViewModel.clean()
        }
    }

    if (leaveViewModel.loading.value) {
        ProgressScreen()
    } else if (leaveViewModel.error.value != null) {
        ErrorScreen(leaveViewModel.error.value!!)
    } else {
        val leaves = leaveViewModel.leaves.value
        var date by remember { mutableStateOf<Date?>(null) }
        var text by remember { mutableStateOf("") }

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
                SearchDateText(
                    date = date,
                    onDateChange = { date = it },
                    text = text,
                    onTextChange = { text = it },
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                leaves.sortedBy { it.start }
                    .reversed()
                    .filter {
                        date?.let { date ->
                            isSameDay(it.start, date) || isSameDay(it.end, date)
                        } ?: true
                    }.filter {
                        val leaveUser = leaveViewModel.findLeaveUser(it)
                        if (text.isNotEmpty()) {
                            it.status.lowercase(Locale.getDefault())
                                .contains(text.lowercase(Locale.getDefault())) ||
                                    leaveUser?.let { user ->
                                        user.name.lowercase(Locale.getDefault())
                                            .contains(text.lowercase(Locale.getDefault())) ||
                                                user.email.lowercase(Locale.getDefault())
                                                    .contains(text.lowercase(Locale.getDefault()))
                                    } ?: true
                        } else {
                            true
                        }
                    }.forEach { leave ->
                        val user = leaveViewModel.findLeaveUser(leave)
                        val (color, title) = when (leave.status) {
                            "PENDING" -> Pair(Orange, "Menunggu Persetujuan")
                            "APPROVED" -> Pair(Color.Green, "Disetujui")
                            else -> Pair(Color.Red, "Ditolak")
                        }
                        StatusCard(
                            title = title,
                            color = color,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .clickable {
                                    leaveViewModel.tempLeave.value = leave
                                    navController.navigate("leave/detail")
                                }
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
                                    "Start: ${formatDate(leave.start, "dd MMMM yyyy")}",
                                )
                                Text(
                                    "End: ${formatDate(leave.end, "dd MMMM yyyy")}",
                                )
                                Text(
                                    "${leave.reason}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray, MaterialTheme.shapes.small)
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
            }
        }
    }
}