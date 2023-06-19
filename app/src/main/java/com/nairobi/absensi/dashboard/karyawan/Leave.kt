package com.nairobi.absensi.dashboard.karyawan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.FabAdd
import com.nairobi.absensi.components.FormText
import com.nairobi.absensi.components.Orange
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.StatusCard
import com.nairobi.absensi.model.LeaveViewModel
import com.nairobi.absensi.utils.datePicker
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.isSameDay
import com.nairobi.absensi.utils.launchBrowser
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Leave(navController: NavController, leaveViewModel: LeaveViewModel) {
    leaveViewModel.refreshLeavesWhere("userId", leaveViewModel.user.value!!.id)

    val context = LocalContext.current

    if (leaveViewModel.loading.value) {
        ProgressScreen()
    } else if (leaveViewModel.error.value != null) {
        ErrorScreen(leaveViewModel.error.value!!)
    } else {
        var filter by remember { mutableStateOf<Date?>(null) }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize()
        ) {
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
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    leaveViewModel.leaves.value.sortedBy { it.start }
                        .reversed()
                        .filter {
                            filter == null || isSameDay(it.start, filter!!) || isSameDay(
                                it.end,
                                filter!!
                            )
                        }
                        .forEach { leave ->
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
                                        if (leave.status == "PENDING") {
                                            leaveViewModel.tempLeave.value = leave
                                            navController.navigate("leave/edit")
                                        }
                                    }
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp)
                                ) {
                                    Text("Start: ${formatDate(leave.start, "dd MMMM yyyy")}")
                                    Text("End: ${formatDate(leave.end, "dd MMMM yyyy")}")
                                    Text(
                                        leave.reason,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                Color.LightGray,
                                                MaterialTheme.shapes.medium
                                            )
                                            .padding(6.dp)
                                    )
                                    if (!leave.document.isNullOrEmpty()) {
                                        Button(
                                            onClick = {
                                                leaveViewModel.getDocumentUrl(leave) {
                                                    it?.let {
                                                        launchBrowser(
                                                            context,
                                                            it
                                                        )
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = Color.White,
                                                containerColor = Purple,
                                            )
                                        ) {
                                            Text("Lihat Dokumen")
                                        }
                                    }
                                }
                            }
                        }
                }
            }
            FabAdd {
                leaveViewModel.tempLeave.value = null
                navController.navigate("leave/add")
            }
        }
    }
}