package com.nairobi.absensi.dashboard.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import com.nairobi.absensi.components.FormText
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.data.Attendance
import com.nairobi.absensi.data.Leave
import com.nairobi.absensi.data.Overtime
import com.nairobi.absensi.model.ExportViewModel
import com.nairobi.absensi.utils.datePicker
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.isBetween
import com.nairobi.absensi.utils.isSameDay
import io.github.evanrupert.excelkt.workbook
import java.io.File
import java.util.Date

@SuppressLint("SetWorldReadable")
fun attendanceExcel(attendances: List<Attendance>): ByteArray {
    val wb = workbook {
        sheet("Absensi") {
            row {
                cell("ID")
                cell("User ID")
                cell("Tanggal")
                cell("Check In")
                cell("Check Out")
                cell("Status")
            }
            attendances.forEach {
                row {
                    cell(it.id)
                    cell(it.userId)
                    cell(formatDate(it.date, "dd MMMM yyyy"))
                    cell(formatDate(it.checkIn, "HH:mm"))
                    cell(formatDate(it.checkOut, "HH:mm"))
                    cell(it.status)
                }
            }
        }
    }
    val file = File.createTempFile("absensi", ".xlsx")
    wb.write(file.absolutePath)
    return file.readBytes()
}

fun leaveExcel(leaves: List<Leave>): ByteArray {
    val wb = workbook {
        sheet("Cuti") {
            row {
                cell("ID")
                cell("User ID")
                cell("Mulai")
                cell("Selesai")
                cell("Alasan")
                cell("Status")
            }
            leaves.forEach {
                row {
                    cell(it.id)
                    cell(it.userId)
                    cell(formatDate(it.start, "dd MMMM yyyy"))
                    cell(formatDate(it.end, "dd MMMM yyyy"))
                    cell(it.reason)
                    cell(it.status)
                }
            }
        }
    }
    val file = File.createTempFile("cuti", ".xlsx")
    wb.write(file.absolutePath)
    return file.readBytes()
}

fun overtimeExcel(overtimes: List<Overtime>): ByteArray {
    val wb = workbook {
        sheet("Lembur") {
            row {
                cell("ID")
                cell("User ID")
                cell("Mulai")
                cell("Selesai")
                cell("Alasan")
                cell("Status")
            }
            overtimes.forEach {
                row {
                    cell(it.id)
                    cell(it.userId)
                    cell(formatDate(it.startTime, "dd MMMM yyyy"))
                    cell(formatDate(it.endTime, "dd MMMM yyyy"))
                    cell(it.reason)
                    cell(it.status)
                }
            }
        }
    }
    val file = File.createTempFile("lembur", ".xlsx")
    wb.write(file.absolutePath)
    return file.readBytes()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Exports(navController: NavController, exportViewModel: ExportViewModel) {
    val context = LocalContext.current
    if (!exportViewModel.loaded.value) {
        exportViewModel.loads()
    }

    BackHandler {
        navController.popBackStack()
    }
    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route != "exports") {
            exportViewModel.clean()
        }
    }

    if (exportViewModel.loading.value) {
        ProgressScreen()
    } else {
        val attendances = exportViewModel.attendances.value
        val overtimes = exportViewModel.overtimes.value
        val leaves = exportViewModel.leaves.value
        val users = exportViewModel.users.value

        var start by remember { mutableStateOf(Date()) }
        var end by remember { mutableStateOf(Date()) }
        var user by remember { mutableStateOf<String>("") }
        var expanded by remember { mutableStateOf(false) }

        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AppBar(navController, "Export Data")
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val filteredAttendances = attendances.filter {
                    val df = isSameDay(it.date, start) || isSameDay(it.date, end) ||
                            isBetween(it.date, start, end)
                    val uf = user == "" || user == it.userId
                    df && uf
                }
                val filteredOvertimes = overtimes.filter {
                    val df = isSameDay(it.startTime, start) || isSameDay(it.startTime, end) ||
                            isBetween(it.startTime, start, end)
                    val uf = user == "" || user == it.userId
                    df && uf
                }
                val filteredLeaves = leaves.filter {
                    val df = isSameDay(it.start, start) || isSameDay(it.start, end) ||
                            isBetween(it.start, start, end)
                    val uf = user == "" || user == it.userId
                    df && uf
                }
                FormText(
                    text = formatDate(start, "dd MMMM yyyy"),
                    label = "Dari Tanggal",
                    disabled = true,
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePicker(context) {
                                start = it
                            }
                        }
                )
                FormText(
                    text = formatDate(end, "dd MMMM yyyy"),
                    label = "Sampai Tanggal",
                    disabled = true,
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePicker(context) {
                                end = it
                            }
                        }
                )
                ExposedDropdownMenuBox(
                    expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                    ) {
                    FormText(
                        text = users.find { it.id == user }?.name ?: "Semua",
                        label = "Karyawan",
                        leadingIcon = {
                            Icon(
                                Icons.Default.People,
                                contentDescription = "User",
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        disabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                    )
                    ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                        exportViewModel.users.value.forEach { v ->
                            DropdownMenuItem(
                                text = {Column(Modifier.fillMaxWidth()) {
                                    Text(text = v.name)
                                    Text(v.email, color = Color.Gray)
                                }},
                                onClick = {
                                    user = v.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        val bytes = attendanceExcel(filteredAttendances)
                        val loading = loadingAlert(context)
                        exportViewModel.uploadExcel("exports/absensi.xlsx", bytes) { url, error ->
                            loading.dismissWithAnimation()
                            if (url != null) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                context.startActivity(intent)
                            } else {
                                errorAlert(
                                    context,
                                    "Gagal",
                                    "Gagal mengupload file: $error"
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Purple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Export absensi: ${filteredAttendances.size}")
                }
                Button(
                    onClick = {
                        val bytes = overtimeExcel(filteredOvertimes)
                        val loading = loadingAlert(context)
                        exportViewModel.uploadExcel("exports/lembur.xlsx", bytes) { url, error ->
                            loading.dismissWithAnimation()
                            if (url != null) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                context.startActivity(intent)
                            } else {
                                errorAlert(
                                    context,
                                    "Gagal",
                                    "Gagal mengupload file: $error"
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Purple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Export lembur: ${filteredOvertimes.size}")
                }
                Button(
                    onClick = {
                        val bytes = leaveExcel(filteredLeaves)
                        val loading = loadingAlert(context)
                        exportViewModel.uploadExcel("exports/cuti.xlsx", bytes) { url, error ->
                            loading.dismissWithAnimation()
                            if (url != null) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                context.startActivity(intent)
                            } else {
                                errorAlert(
                                    context,
                                    "Gagal",
                                    "Gagal mengupload file: $error"
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Purple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Export cuti: ${filteredLeaves.size}")
                }
            }
        }
    }
}