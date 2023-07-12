package com.nairobi.absensi.model

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nairobi.absensi.data.Attendance
import com.nairobi.absensi.data.CheckinCheckoutState
import com.nairobi.absensi.data.EarlyCheckout
import com.nairobi.absensi.data.Office
import com.nairobi.absensi.data.User
import com.nairobi.absensi.repo.AttendanceRepository
import com.nairobi.absensi.repo.EarlyCheckoutRepository
import com.nairobi.absensi.repo.HolidayRepository
import com.nairobi.absensi.repo.OfficeRepository
import com.nairobi.absensi.repo.StorageRepository
import com.nairobi.absensi.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val userRepository: UserRepository,
    private val officeRepository: OfficeRepository,
    private val storageRepository: StorageRepository,
    private val holidayRepository: HolidayRepository,
    private val earlyCheckoutRepository: EarlyCheckoutRepository,
) : ViewModel() {
    val users: MutableState<List<User>> = mutableStateOf(emptyList())
    val user: MutableState<User?> = mutableStateOf(null)
    val attendances: MutableState<List<Attendance>> = mutableStateOf(emptyList())
    val earlyCheckouts: MutableState<List<EarlyCheckout>> = mutableStateOf(emptyList())
    val error: MutableState<String?> = mutableStateOf(null)
    val loading: MutableState<Boolean> = mutableStateOf(false)
    val office: MutableState<Office?> = mutableStateOf(null)
    val tempAttendance: MutableState<Attendance?> = mutableStateOf(null)
    val holidays: MutableState<List<Date>> = mutableStateOf(emptyList())
    val face: MutableState<Bitmap?> = mutableStateOf(null)
    val faceLive: MutableList<Bitmap> = mutableListOf()
    val checkinCheckoutPrepared: MutableState<Boolean> = mutableStateOf(false)
    val checkinCheckoutState: MutableState<CheckinCheckoutState> =
        mutableStateOf(CheckinCheckoutState.START)

    fun getAttendances(onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = attendanceRepository.getAttendances()
            attendances.value = result.data ?: emptyList()
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun getAttendancesWhere(field: String, value: String, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = attendanceRepository.getAttendancesWhere(field, value)
            attendances.value = result.data ?: emptyList()
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun refreshAttendances() {
        viewModelScope.launch {
            office.value = officeRepository.getOffice()
            if (users.value.size != userRepository.countUsers()) {
                getUsers()
            }
            if (attendances.value.size != attendanceRepository.countAttendances()) {
                getAttendances()
            }
        }
    }

    fun refreshAttendancesWhere(field: String, value: String) {
        viewModelScope.launch {
            office.value = officeRepository.getOffice()
            if (attendances.value.size != attendanceRepository.countAttendancesWhere(
                    field,
                    value
                )
            ) {
                getAttendancesWhere(field, value)
            }
        }
    }

    fun prepareCheckInCheckOut() {
        viewModelScope.launch {
            if (checkinCheckoutPrepared.value) {
                return@launch
            }
            loading.value = true
            attendances.value = attendanceRepository.getAttendancesWhere(
                "userId",
                user.value?.id ?: ""
            ).data ?: emptyList()
            earlyCheckouts.value = earlyCheckoutRepository.getEarlyCheckoutsWhere(
                "userId",
                user.value?.id ?: ""
            ).data ?: emptyList()
            office.value = officeRepository.getOffice()
            holidays.value = holidayRepository.getHolidays().data ?: emptyList()
            face.value = storageRepository.getBitmap(user.value!!.face!!).data
            checkinCheckoutPrepared.value = true
            checkinCheckoutState.value = CheckinCheckoutState.START
            loading.value = false
        }
    }

    fun isCheckInCheckOutPrepared(): Boolean {
        return face.value != null
    }

    fun cleanCheckInCheckOut() {
        viewModelScope.launch {
            if (!checkinCheckoutPrepared.value) {
                return@launch
            }
            loading.value = true
            holidays.value = emptyList()
            face.value = null
            faceLive.clear()
            checkinCheckoutPrepared.value = false
            checkinCheckoutState.value = CheckinCheckoutState.START
            loading.value = false
        }
    }

    fun isCCState(state: CheckinCheckoutState): Boolean {
        return checkinCheckoutState.value == state
    }

    fun ccState(state: CheckinCheckoutState) {
        checkinCheckoutState.value = state
    }

    fun launchScope(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    fun clean() {
        users.value = emptyList()
        attendances.value = emptyList()
        error.value = null
        loading.value = false
        tempAttendance.value = null
    }

    fun findAttendanceUser(attendance: Attendance): User? {
        return users.value.find { it.id == attendance.userId }
    }

    fun getUsers(onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = userRepository.getUsers()
            users.value = result.data ?: emptyList()
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun getEarlyCheckouts(onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = earlyCheckoutRepository.getEarlyCheckouts()
            earlyCheckouts.value = result.data ?: emptyList()
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun addAttendance(attendance: Attendance, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = attendanceRepository.addAttendance(attendance)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun addEarlyCheckout(earlyCheckout: EarlyCheckout, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = earlyCheckoutRepository.addEarlyCheckout(earlyCheckout)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun updateAttendance(attendance: Attendance, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = attendanceRepository.updateAttendance(attendance)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun updateEarlyCheckout(earlyCheckout: EarlyCheckout, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = earlyCheckoutRepository.updateEarlyCheckout(earlyCheckout)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun deleteAttendance(attendance: Attendance, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = attendanceRepository.deleteAttendance(attendance)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun deleteEarlyCheckout(earlyCheckout: EarlyCheckout, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = earlyCheckoutRepository.deleteEarlyCheckout(earlyCheckout)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }
}