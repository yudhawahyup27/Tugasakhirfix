package com.nairobi.absensi.dashboard.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Newspaper
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
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.components.successAlert
import com.nairobi.absensi.data.Overtime
import com.nairobi.absensi.data.User
import com.nairobi.absensi.model.OvertimeViewModel
import com.nairobi.absensi.utils.dateTimePicker
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.isSameDay
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOvertime(navController: NavController, overtimeViewModel: OvertimeViewModel) {
    val context = LocalContext.current

    var selectedUser by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<User?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var start by remember { mutableStateOf(Date()) }
    var end by remember { mutableStateOf(Date()) }
    var reason by remember { mutableStateOf("") }

    fun save() {
        if (selected == null) {
            errorAlert(
                context,
                "Gagal",
                "Karyawan tidak boleh kosong"
            )
            return
        }
        val conflict = overtimeViewModel.overtimes.value.find { v ->
            v.userId == selected!!.id && (isSameDay(v.startTime, start))
        }
        if (conflict != null) {
            errorAlert(
                context,
                "Gagal",
                "Karyawan sudah memiliki lembur pada tanggal tersebut"
            )
            return
        }
        var holiday = false
        overtimeViewModel.holidays.value.forEach { v ->
            if (isSameDay(v, start)) {
                holiday = true
            }
        }
        if (holiday) {
            errorAlert(
                context,
                "Gagal",
                "Karyawan tidak boleh lembur pada hari libur"
            )
            return
        }
        if (start.after(end)) {
            errorAlert(
                context,
                "Gagal",
                "Waktu mulai tidak boleh setelah waktu selesai"
            )
            return
        }
        if (reason.isEmpty()) {
            errorAlert(
                context,
                "Gagal",
                "Alasan tidak boleh kosong"
            )
            return
        }
        val data = Overtime(
            startTime = start,
            endTime = end,
            reason = reason,
            userId = selected!!.id,
        )
        val loading = loadingAlert(context)
        overtimeViewModel.addOvertime(data) {
            loading.dismissWithAnimation()
            successAlert(
                context,
                "Berhasil",
                "Berhasil menambahkan lembur"
            ) {
                navController.popBackStack()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBar(navController, "Tambah Lembur")
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                FormText(
                    text = selectedUser,
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
                    // iterate over map overtimeViewModel.users
                    overtimeViewModel.users.value.filter {
                        it.role == "KARYAWAN"
                    }
                        .sortedBy { it.name }
                        .forEach { v ->
                            DropdownMenuItem(text = {
                                Column(Modifier.fillMaxWidth()) {
                                    Text(text = v.name)
                                    Text(v.email, color = Color.Gray)
                                }
                            }, onClick = {
                                selectedUser = v.name
                                selected = v
                                expanded = false
                            })
                        }
                }
            }
            FormText(
                text = formatDate(start, "dd/MM/yyyy HH:mm"),
                label = "Mulai",
                leadingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Start",
                    )
                },
                disabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        dateTimePicker(context) { date ->
                            start = date
                        }
                    }
            )
            FormText(
                text = formatDate(end, "dd/MM/yyyy HH:mm"),
                label = "Selesai",
                leadingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "End",
                    )
                },
                disabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        dateTimePicker(context) { date ->
                            end = date
                        }
                    }
            )
            FormText(
                text = reason,
                label = "Alasan",
                leadingIcon = {
                    Icon(
                        Icons.Default.Newspaper,
                        contentDescription = "Reason",
                    )
                },
                onTextChange = { reason = it },
                wrap = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Button(
                onClick = { save() },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Purple,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
            ) {
                Text(text = "Tambah")
            }
        }
    }
}