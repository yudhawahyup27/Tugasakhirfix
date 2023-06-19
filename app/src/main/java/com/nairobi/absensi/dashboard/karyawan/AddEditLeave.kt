package com.nairobi.absensi.dashboard.karyawan

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.nairobi.absensi.components.PdfPickerLauncher
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.components.successAlert
import com.nairobi.absensi.components.warningAlert
import com.nairobi.absensi.data.Leave
import com.nairobi.absensi.model.LeaveViewModel
import com.nairobi.absensi.utils.datePicker
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.isBetween
import com.nairobi.absensi.utils.isToday
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEditLeave(
    navController: NavController,
    leaveViewModel: LeaveViewModel,
    edit: Boolean = false
) {
    val context = LocalContext.current

    val temp = leaveViewModel.tempLeave.value ?: Leave()

    var start by remember { mutableStateOf(temp.start) }
    var end by remember { mutableStateOf(temp.end) }
    var reason by remember { mutableStateOf(temp.reason) }
    var reasonError by remember { mutableStateOf(false) }
    var file by remember { mutableStateOf(temp.document) }

    val pdfPickerLauncher = PdfPickerLauncher(context as Activity) {
        file = it
    }

    fun save() {
        if (reason.isEmpty()) {
            reasonError = true
            return
        }
        if (isToday(start) || isToday(end)) {
            errorAlert(
                context,
                "Gagal mengajukan cuti",
                "Silahkan mengajukan cuti minimal 1 hari sebelumnya"
            )
            return
        }
        if (start.after(end) || Date().after(start) || Date().after(end)) {
            errorAlert(
                context,
                "Gagal mengajukan cuti",
                "Silahkan memilih tanggal yang valid"
            )
            return
        }
        var leaves = leaveViewModel.leaves.value
        if (edit) {
            leaves = leaves.filter { it.id != temp.id }
        }
        val pending = leaves.filter { it.status == "PENDING" }
        if (pending.isNotEmpty()) {
            errorAlert(
                context,
                "Gagal mengajukan cuti",
                "Silahkan menunggu cuti anda yang sebelumnya diproses"
            )
            return
        }
        val dateConflict = leaves.filter {
            isBetween(start, it.start, it.end) ||
                    isBetween(end, it.start, it.end) ||
                    isBetween(it.start, start, end) ||
                    isBetween(it.end, start, end)
        }
        if (dateConflict.isNotEmpty()) {
            errorAlert(
                context,
                "Gagal mengajukan cuti",
                "Silahkan memilih tanggal yang tidak bentrok dengan cuti anda yang sebelumnya"
            )
            return
        }
        val leaveData = Leave(
            start = start,
            end = end,
            reason = reason,
            userId = leaveViewModel.user.value?.id ?: ""
        )
        if (!file.isNullOrEmpty()) {
            leaveData.document = file
        }
        if (edit) {
            leaveData.id = temp.id
        }
        val loading = loadingAlert(context)
        if (!edit) {
            leaveViewModel.addLeave(leaveData) {
                loading.dismissWithAnimation()
                successAlert(
                    context,
                    "Berhasil mengajukan cuti",
                    "Cuti anda akan diproses oleh admin"
                ) {
                    navController.popBackStack()
                }
            }
        } else {
            leaveViewModel.updateLeave(leaveData) {
                loading.dismissWithAnimation()
                successAlert(
                    context,
                    "Berhasil mengubah cuti",
                    "Cuti anda akan diproses oleh admin"
                ) {
                    leaveViewModel.getLeavesWhere("userId", leaveViewModel.user.value?.id ?: "")
                    navController.popBackStack()
                }
            }
        }
    }

    fun delete() {
        val loading = loadingAlert(context)
        leaveViewModel.deleteLeave(temp) {
            loading.dismissWithAnimation()
            successAlert(
                context,
                "Berhasil menghapus cuti",
                "Cuti anda akan diproses oleh admin"
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
        AppBar(navController, if (edit) "Ubah Cuti" else "Ajukan Cuti")
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            FormText(
                text = formatDate(start, "dd MMMM yyyy"),
                label = "Dari Tanggal",
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                disabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        datePicker(context) { date ->
                            start = date
                        }
                    }
            )
            FormText(
                text = formatDate(end, "dd MMMM yyyy"),
                label = "Sampai Tanggal",
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                disabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        datePicker(context) { date ->
                            end = date
                        }
                    }
            )
            FormText(
                text = reason,
                label = "Alasan",
                leadingIcon = {
                    Icon(Icons.Default.TextFields, contentDescription = null)
                },
                onTextChange = { reason = it },
                textError = reasonError,
                supportingText = "Alasan tidak boleh kosong",
                onTextErrorChange = { reasonError = it },
                wrap = true,
                validator = { it.isNotEmpty() },
                modifier = Modifier.fillMaxWidth()
            )
            FormText(
                text = file ?: "",
                label = "Dokumen Pendukung",
                leadingIcon = {
                    Icon(Icons.Default.Newspaper, contentDescription = null)
                },
                disabled = true,
                wrap = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        pdfPickerLauncher()
                    }
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = { save() },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Purple,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(if (edit) "Ubah" else "Ajukan")
                }
                if (edit) {
                    Button(
                        onClick = {
                            warningAlert(
                                context,
                                "Hapus cuti",
                                "Apakah anda yakin ingin menghapus cuti ini?",
                                positiveAction = { delete() }
                            )
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Hapus")
                    }
                }
            }
        }
    }
}