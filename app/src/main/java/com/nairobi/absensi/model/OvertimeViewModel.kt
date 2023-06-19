package com.nairobi.absensi.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nairobi.absensi.data.Office
import com.nairobi.absensi.data.Overtime
import com.nairobi.absensi.data.User
import com.nairobi.absensi.repo.HolidayRepository
import com.nairobi.absensi.repo.OfficeRepository
import com.nairobi.absensi.repo.OvertimeRepository
import com.nairobi.absensi.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OvertimeViewModel @Inject constructor(
    private val overtimeRepository: OvertimeRepository,
    private val userRepository: UserRepository,
    private val holidayRepository: HolidayRepository,
    private val officeRepository: OfficeRepository,
) : ViewModel() {
    val user: MutableState<User?> = mutableStateOf(null)
    val users: MutableState<List<User>> = mutableStateOf(emptyList())
    val overtimes: MutableState<List<Overtime>> = mutableStateOf(emptyList())
    val holidays: MutableState<List<Date>> = mutableStateOf(emptyList())
    val office: MutableState<Office?> = mutableStateOf(null)
    val error: MutableState<String?> = mutableStateOf(null)
    val loading: MutableState<Boolean> = mutableStateOf(false)
    val tempOvertime: MutableState<Overtime?> = mutableStateOf(null)

    fun getOvertimes(onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = overtimeRepository.getOvertimes()
            overtimes.value = result.data ?: emptyList()
            error.value = result.error
            loading.value = false
            onFinish()
        }
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

    fun getOvertimesWhere(field: String, value: String, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = overtimeRepository.getOvertimesWhere(field, value)
            overtimes.value = result.data ?: emptyList()
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun refreshOvertimes() {
        viewModelScope.launch {
            if (office.value == null) {
                office.value = officeRepository.getOffice()
            }
            val result = holidayRepository.getHolidays()
            if (result.error == null) {
                holidays.value = result.data ?: emptyList()
            }
            if (users.value.size != userRepository.countUsers()) {
                getUsers()
            }
            if (overtimes.value.size != overtimeRepository.countOvertimes()) {
                getOvertimes()
            }
        }
    }

    fun findOvertimeUser(overtime: Overtime): User? {
        return users.value.find { it.id == overtime.userId }
    }

    fun refreshOvertimesWhere(field: String, value: String) {
        viewModelScope.launch {
            if (office.value == null) {
                office.value = officeRepository.getOffice()
            }
            val result = holidayRepository.getHolidays()
            if (result.error == null) {
                holidays.value = result.data ?: emptyList()
            }
            if (overtimes.value.size != overtimeRepository.countOvertimesWhere(field, value)) {
                getOvertimesWhere(field, value)
            }
        }
    }

    fun clean() {
        users.value = emptyList()
        overtimes.value = emptyList()
        error.value = null
        loading.value = false
        tempOvertime.value = null
    }

    fun addOvertime(overtime: Overtime, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = overtimeRepository.addOvertime(overtime)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun updateOvertime(overtime: Overtime, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = overtimeRepository.updateOvertime(overtime)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun deleteOvertime(overtime: Overtime, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = overtimeRepository.deleteOvertime(overtime.id)
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }
}