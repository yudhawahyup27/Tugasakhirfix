package com.nairobi.absensi.dashboard.karyawan

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.components.successAlert
import com.nairobi.absensi.data.EarlyCheckout
import com.nairobi.absensi.model.AttendanceViewModel
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.isToday
import java.util.Date

@Composable
fun Checkout(navController: NavController, attendanceViewModel: AttendanceViewModel) {
    val context = LocalContext.current
    attendanceViewModel.prepareCheckInCheckOut()

    var lock by remember { mutableStateOf(false) }

    BackHandler {
        navController.popBackStack()
    }
    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route != "checkout") {
            attendanceViewModel.cleanCheckInCheckOut()
        }
    }

    if (attendanceViewModel.loading.value) {
        ProgressScreen()
    } else if (!lock) {
        lock = true
        val checkIn =
            attendanceViewModel.attendances.value.find { it.status == "CHECKIN" && isToday(it.date) }
        if (checkIn == null) {
            errorAlert(
                context = context,
                "Checkout Gagal",
                "Anda belum melakukan checkin"
            ) {
                navController.popBackStack()
            }
        } else {
            val office = attendanceViewModel.office.value!!
            val close = office.closeTime
            val end = Date()
            end.hours = close.hours
            end.minutes = close.minutes
            if (Date().before(end)) {
                errorAlert(
                    context = context,
                    "Checkout Gagal",
                    "Anda belum bisa checkout, silahkan coba lagi setelah ${
                        formatDate(
                            end,
                            "HH:mm"
                        )
                    }",
                    {
                        val current = attendanceViewModel.earlyCheckouts.value.find { isToday(it.date) }
                        if (current != null) {
                            errorAlert(
                                context = context,
                                "Checkout Gagal",
                                "Anda sudah mengajukan izin pulang awal"
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            val early = EarlyCheckout(
                                userId = attendanceViewModel.user.value!!.id,
                                date = Date(),
                            )
                            attendanceViewModel.addEarlyCheckout(early) {
                                if (attendanceViewModel.error.value != null) {
                                    errorAlert(
                                        context = context,
                                        "Checkout Gagal",
                                        attendanceViewModel.error.value!!
                                    ) {
                                        navController.popBackStack()
                                    }
                                } else {
                                    successAlert(
                                        context = context,
                                        "Checkout Berhasil",
                                        "Anda telah mengajukan izin pulang awal"
                                    ) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    },
                    "Izin pulang awal",
                    true,
                ) {
                    navController.popBackStack()
                }
            } else {
                checkIn.status = "CHECKOUT"
                checkIn.checkOut = Date()
                attendanceViewModel.updateAttendance(checkIn) {
                    successAlert(
                        context = context,
                        "Checkout Berhasil",
                        "Anda telah checkout pada ${formatDate(checkIn.date, "HH:mm")}"
                    ) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}