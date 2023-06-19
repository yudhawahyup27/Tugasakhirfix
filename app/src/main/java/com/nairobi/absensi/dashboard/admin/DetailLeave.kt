package com.nairobi.absensi.dashboard.admin

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.FormText
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.model.LeaveViewModel
import com.nairobi.absensi.utils.formatDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailLeave(navController: NavController, leaveViewModel: LeaveViewModel) {
    val leave = leaveViewModel.tempLeave.value
    if (leave == null) {
        navController.popBackStack()
    }

    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBar(navController, "Detail Cuti")
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            leave?.let {
                val user = leaveViewModel.findLeaveUser(it)
                user?.let { user ->
                    FormText(
                        text = user.name,
                        label = "Nama",
                        disabled = true,
                        leadingIcon = {
                            Icon(Icons.Default.CreditCard, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    FormText(
                        text = user.email,
                        label = "Email",
                        disabled = true,
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                FormText(
                    text = formatDate(it.start, "dd MMMM yyyy"),
                    label = "Tanggal Mulai",
                    disabled = true,
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                FormText(
                    text = formatDate(it.end, "dd MMMM yyyy"),
                    label = "Tanggal Selesai",
                    disabled = true,
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                FormText(
                    text = it.reason,
                    label = "Alasan",
                    disabled = true,
                    leadingIcon = {
                        Icon(Icons.Default.Newspaper, contentDescription = null)
                    },
                    wrap = true,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (it.document != null) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple,
                            contentColor = Color.White,
                        ),
                        onClick = {
                            leaveViewModel.getDocumentUrl(it) { url ->
                                url?.let {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse(it)
                                    context.startActivity(intent)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("Lihat lampiran")
                    }
                }
                if (it.status == "PENDING") {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp)
                    ) {
                        Button(
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Green,
                                contentColor = Color.White,
                            ),
                            onClick = {
                                val loading = loadingAlert(context)
                                leaveViewModel.approveLeave(it) {
                                    loading.dismiss()
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Text("Setujui")
                        }
                        Button(
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White,
                            ),
                            onClick = {
                                it.status = "REJECTED"
                                val loading = loadingAlert(context)
                                leaveViewModel.updateLeave(it) {
                                    loading.dismiss()
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text("Tolak")
                        }
                    }
                }
            }
        }
    }
}