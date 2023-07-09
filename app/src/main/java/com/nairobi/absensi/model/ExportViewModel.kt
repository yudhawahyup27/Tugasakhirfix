package com.nairobi.absensi.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nairobi.absensi.data.Attendance
import com.nairobi.absensi.data.Leave
import com.nairobi.absensi.data.Overtime
import com.nairobi.absensi.data.User
import com.nairobi.absensi.repo.AttendanceRepository
import com.nairobi.absensi.repo.LeaveRepository
import com.nairobi.absensi.repo.OvertimeRepository
import com.nairobi.absensi.repo.StorageRepository
import com.nairobi.absensi.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val overtimeRepository: OvertimeRepository,
    private val leaveRepository: LeaveRepository,
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val attendances: MutableState<List<Attendance>> = mutableStateOf(emptyList())
    val overtimes: MutableState<List<Overtime>> = mutableStateOf(emptyList())
    val leaves: MutableState<List<Leave>> = mutableStateOf(emptyList())
    val users: MutableState<List<User>> = mutableStateOf(emptyList())
    val loading: MutableState<Boolean> = mutableStateOf(false)
    val loaded: MutableState<Boolean> = mutableStateOf(false)

    fun loads() {
        viewModelScope.launch {
            loading.value = true
            attendances.value = attendanceRepository.getAttendances().data ?: emptyList()
            overtimes.value = overtimeRepository.getOvertimes().data ?: emptyList()
            leaves.value = leaveRepository.getLeaves().data ?: emptyList()
            users.value = userRepository.getUsersWhere("role", "KARYAWAN").data ?: emptyList()
            loading.value = false
            loaded.value = true
        }
    }

    fun uploadExcel(
        path: String,
        file: ByteArray,
        onFinish: (String?, String?) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val result = storageRepository.uploadExcel(path, file)
            onFinish(result.data, result.error)
        }
    }

    fun clean() {
        attendances.value = emptyList()
        overtimes.value = emptyList()
        leaves.value = emptyList()
        loading.value = false
    }
}