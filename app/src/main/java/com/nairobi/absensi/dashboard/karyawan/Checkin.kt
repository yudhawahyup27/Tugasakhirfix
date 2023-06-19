package com.nairobi.absensi.dashboard.karyawan

import android.app.Activity
import android.graphics.BitmapFactory
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
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.components.successAlert
import com.nairobi.absensi.data.CheckinCheckoutState
import com.nairobi.absensi.model.AttendanceViewModel
import com.nairobi.absensi.neural.FaceDetector
import com.nairobi.absensi.neural.FaceRecognition
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.getCurrentLocationSuspend
import com.nairobi.absensi.utils.getDistance
import com.nairobi.absensi.utils.isSunday
import com.nairobi.absensi.utils.isToday
import com.nairobi.absensi.utils.subMinute
import com.schaefer.livenesscamerax.domain.model.CameraLens
import com.schaefer.livenesscamerax.domain.model.StepLiveness
import com.schaefer.livenesscamerax.presentation.model.CameraSettings
import com.schaefer.livenesscamerax.presentation.navigation.LivenessEntryPoint
import java.util.Base64
import java.util.Date

@Composable
fun Checkin(navController: NavController, attendanceViewModel: AttendanceViewModel) {
    val context = LocalContext.current
    attendanceViewModel.prepareCheckInCheckOut()

    var lock by remember { mutableStateOf(false) }

    BackHandler {
        navController.popBackStack()
    }
    navController.addOnDestinationChangedListener { _, dest, _ ->
        if (dest.route != "checkin") {
            attendanceViewModel.cleanCheckInCheckOut()
        }
    }

    if (attendanceViewModel.loading.value || !attendanceViewModel.checkinCheckoutPrepared.value) {
        ProgressScreen()
    } else {
        when (attendanceViewModel.checkinCheckoutState.value) {
            CheckinCheckoutState.START -> {
                if (!lock) {
                    lock = true
                    val loading = loadingAlert(context)
                    if (!attendanceViewModel.isCheckInCheckOutPrepared()) {
                        loading.dismiss()
                        errorAlert(
                            context,
                            "Gagal",
                            "Silahkan laporkan kepada admin untuk memperbaiki data anda"
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        val conflict =
                            attendanceViewModel.attendances.value.find { isToday(it.date) }
                        if (conflict != null) {
                            loading.dismiss()
                            errorAlert(
                                context,
                                "Gagal",
                                "Anda sudah melakukan absensi hari ini"
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            loading.dismiss()
                            lock = false
                            attendanceViewModel.ccState(CheckinCheckoutState.HOLIDAY)
                        }
                    }
                }
            }

            CheckinCheckoutState.HOLIDAY -> {
                if (!lock) {
                    val loading = loadingAlert(context)
                    lock = true
                    val holiday = attendanceViewModel.holidays.value.find { isToday(it) }
                    if (holiday != null) {
                        loading.dismiss()
                        errorAlert(
                            context,
                            "Gagal",
                            "Hari ini adalah hari libur"
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        lock = false
                        loading.dismiss()
                        attendanceViewModel.ccState(CheckinCheckoutState.SUNDAY)
                    }
                }
            }

            CheckinCheckoutState.SUNDAY -> {
                if (!lock) {
                    val loading = loadingAlert(context)
                    lock = true
                    if (isSunday(Date())) {
                        loading.dismiss()
                        errorAlert(
                            context,
                            "Gagal",
                            "Hari ini adalah hari minggu"
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        lock = false
                        loading.dismiss()
                        attendanceViewModel.ccState(CheckinCheckoutState.STARTTIME)
                    }
                }
            }

            CheckinCheckoutState.STARTTIME -> {
                if (!lock) {
                    val loading = loadingAlert(context)
                    lock = true
                    val open = attendanceViewModel.office.value!!.openTime
                    val close = attendanceViewModel.office.value!!.closeTime
                    val start = Date()
                    val end = Date()
                    start.hours = open.hours
                    start.minutes = open.minutes
                    end.hours = close.hours
                    end.minutes = close.minutes
                    val allowed = subMinute(start, 30)
                    if (Date().before(allowed)) {
                        loading.dismiss()
                        errorAlert(
                            context,
                            "Gagal",
                            "Anda belum bisa melakukan absensi, waktu buka kantor adalah ${
                                formatDate(
                                    start,
                                    "HH:mm"
                                )
                            } dan anda baru bisa melakukan absensi 30 menit sebelumnya yaitu pada ${
                                formatDate(
                                    allowed,
                                    "HH:mm"
                                )
                            }"
                        ) {
                            navController.popBackStack()
                        }
                    } else if (Date().after(end)) {
                        attendanceViewModel.addAttendance(
                            com.nairobi.absensi.data.Attendance(
                                userId = attendanceViewModel.user.value!!.id,
                                status = "ABSENT",
                            )
                        )
                        loading.dismiss()
                        errorAlert(
                            context,
                            "Gagal",
                            "Anda sudah melewatkan waktu absensi, waktu tutup kantor adalah ${
                                formatDate(
                                    end,
                                    "HH:mm"
                                )
                            }"
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        lock = false
                        loading.dismiss()
                        attendanceViewModel.ccState(CheckinCheckoutState.LOCATION)
                    }
                }
            }

            CheckinCheckoutState.LOCATION -> {
                if (!lock) {
                    val loading = loadingAlert(context)
                    lock = true
                    attendanceViewModel.launchScope {
                        val office = attendanceViewModel.office.value!!
                        val location = getCurrentLocationSuspend(context as Activity)
                        val distance = getDistance(
                            location.first,
                            location.second,
                            office.address.latitude,
                            office.address.longitude
                        )
                        if (distance > 500) {
                            loading.dismiss()
                            errorAlert(
                                context,
                                "Gagal",
                                "Anda berada di luar jangkauan kantor, jarak anda dengan kantor adalah ${distance} meter"
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            lock = false
                            loading.dismiss()
                            attendanceViewModel.ccState(CheckinCheckoutState.LIVENESS)
                        }
                    }
                }
            }

            CheckinCheckoutState.LIVENESS -> {
                if (!lock) {
                    val loading = loadingAlert(context)
                    lock = true
                    LivenessEntryPoint.startLiveness(
                        context = context,
                        cameraSettings = CameraSettings(
                            cameraLens = CameraLens.DEFAULT_FRONT_CAMERA,
                            livenessStepList = arrayListOf(
                                StepLiveness.STEP_LUMINOSITY,
                                StepLiveness.STEP_BLINK,
                                StepLiveness.STEP_SMILE,
                                StepLiveness.STEP_HEAD_RIGHT,
                                StepLiveness.STEP_HEAD_LEFT,
                            ),
                        )
                    ) { result ->
                        if (result.error != null || result.createdBySteps == null || result.createdBySteps!!.isEmpty()) {
                            loading.dismiss()
                            errorAlert(
                                context,
                                "Gagal",
                                "Terjadi kesalahan saat melakukan liveness"
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            val total = result.createdBySteps?.size ?: 0
                            var tryings = 0
                            result.createdByUser?.let {
                                val face = Base64.getDecoder().decode(it.fileBase64)
                                val faceBitmap =
                                    BitmapFactory.decodeByteArray(face, 0, face.size)
                                attendanceViewModel.faceLive.add(faceBitmap)
                            }
                            result.createdBySteps!!.forEach { res ->
                                tryings++
                                val face = Base64.getDecoder().decode(res.fileBase64)
                                loading.setTitleText("Mengenali wajah ($tryings/$total) ${face.size}")
                                val faceBitmap =
                                    BitmapFactory.decodeByteArray(face, 0, face.size)
                                attendanceViewModel.faceLive.add(faceBitmap)
                            }
                            loading.dismiss()
                            lock = false
                            attendanceViewModel.ccState(CheckinCheckoutState.FACE)
                        }
                    }
                }
            }

            CheckinCheckoutState.FACE -> {
                if (!lock) {
                    lock = true
                    attendanceViewModel.launchScope {
                        val loading = loadingAlert(context)
                        val face = attendanceViewModel.face.value!!
                        val faceLive = attendanceViewModel.faceLive
                        val recognition = FaceRecognition(context, 112)
                        val detector = FaceDetector()
                        val target = recognition.recognize(face)[0]
                        recognition.register("target", target)
                        val similarities = arrayListOf<Float>()
                        faceLive.forEach { live ->
                            val detected = detector.detect(live)
                            detected?.let { det ->
                                val sim = recognition.recognize(det)[0].distance
                                similarities.add(sim)
                            }
                        }
                        val smallest = similarities.minOrNull() ?: 1f
                        if (smallest > 0.9f) {
                            loading.dismiss()
                            errorAlert(
                                context,
                                "Gagal",
                                "Wajah anda tidak cocok dengan wajah yang terdaftar"
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            loading.dismiss()
                            lock = false
                            attendanceViewModel.ccState(CheckinCheckoutState.SAVE)
                        }
                    }
                }
            }

            CheckinCheckoutState.SAVE -> {
                if (!lock) {
                    lock = true
                    val loading = loadingAlert(context)
                    attendanceViewModel.addAttendance(
                        com.nairobi.absensi.data.Attendance(
                            userId = attendanceViewModel.user.value!!.id,
                            status = "CHECKIN",
                        )
                    ) {
                        loading.dismiss()
                        successAlert(context, "Berhasil", "Anda berhasil melakukan absensi") {
                            navController.popBackStack()
                        }
                    }
                }
            }

            else -> {}
        }
    }
}