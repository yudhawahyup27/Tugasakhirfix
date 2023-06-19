package com.nairobi.absensi.model

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nairobi.absensi.data.Attendance
import com.nairobi.absensi.data.Leave
import com.nairobi.absensi.data.User
import com.nairobi.absensi.repo.AttendanceRepository
import com.nairobi.absensi.repo.LeaveRepository
import com.nairobi.absensi.repo.StorageRepository
import com.nairobi.absensi.repo.UserRepository
import com.nairobi.absensi.utils.addOneDay
import com.nairobi.absensi.utils.getDayDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveViewModel @Inject constructor(
    private val leaveRepository: LeaveRepository,
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository,
    private val attendanceRepository: AttendanceRepository,
) : ViewModel() {
    val user: MutableState<User?> = mutableStateOf(null)
    val users: MutableState<List<User>> = mutableStateOf(emptyList())
    val leaves: MutableState<List<Leave>> = mutableStateOf(emptyList())
    val error: MutableState<String?> = mutableStateOf(null)
    val loading: MutableState<Boolean> = mutableStateOf(false)
    val tempLeave: MutableState<Leave?> = mutableStateOf(null)

    fun getLeaves(onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = leaveRepository.getLeaves()
            leaves.value = result.data ?: emptyList()
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

    fun getLeavesWhere(field: String, value: String, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = leaveRepository.getLeavesWhere(field, value)
            leaves.value = result.data ?: emptyList()
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun refreshLeaves() {
        viewModelScope.launch {
            if (users.value.size != userRepository.countUsers()) {
                getUsers()
            }
            if (leaves.value.size != leaveRepository.countLeaves()) {
                getLeaves()
            }
        }
    }

    fun refreshLeavesWhere(field: String, value: String) {
        viewModelScope.launch {
            if (leaves.value.size != leaveRepository.countLeavesWhere(field, value)) {
                getLeavesWhere(field, value)
            }
        }
    }

    fun clean() {
        users.value = emptyList()
        leaves.value = emptyList()
        error.value = null
        loading.value = false
        tempLeave.value = null
    }

    fun findLeaveUser(leave: Leave): User? {
        return users.value.find { it.id == leave.userId }
    }

    fun addLeave(leave: Leave, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = leaveRepository.addLeave(leave)
            if (result.error == null && !leave.document.isNullOrEmpty()) {
                val uri = Uri.parse(leave.document)
                val res2 = storageRepository.uploadFile("documents/${leave.id}", uri)
                if (res2.error == null) {
                    leave.document = res2.data.toString()
                    leaveRepository.updateLeave(leave)
                }
            }
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun updateLeave(leave: Leave, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = leaveRepository.updateLeave(leave)
            if (result.error == null && leave.document != null) {
                if (leave.document != "documents/${leave.id}") {
                    val uri = Uri.parse(leave.document)
                    val res2 = storageRepository.uploadFile("documents/${leave.id}", uri)
                    if (res2.error == null) {
                        leave.document = res2.data.toString()
                        leaveRepository.updateLeave(leave)
                    }
                }
            }
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun approveLeave(leave: Leave, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            var initialAttendance = Attendance(
                id = "",
                userId = leave.userId,
                date = leave.start,
                checkIn = leave.start,
                checkOut = leave.start,
                status = "LEAVE",
            )
            attendanceRepository.addAttendance(initialAttendance)
            var dayDistance = getDayDistance(leave.end, leave.start)
            while (dayDistance > 0) {
                initialAttendance = Attendance(
                    id = "",
                    userId = leave.userId,
                    date = addOneDay(initialAttendance.date),
                    checkIn = addOneDay(initialAttendance.date),
                    checkOut = addOneDay(initialAttendance.date),
                    status = "LEAVE",
                )
                attendanceRepository.addAttendance(initialAttendance)
                dayDistance--
            }
            leave.status = "APPROVED"
            updateLeave(leave)
            onFinish()
        }
    }

    fun deleteLeave(leave: Leave, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = leaveRepository.deleteLeave(leave.id)
            leave.document?.let {
                storageRepository.deleteFile(it)
            }
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun getDocumentUrl(leave: Leave, onFinish: (String?) -> Unit = {}) {
        viewModelScope.launch {
            leave.document?.let {
                val result = storageRepository.getDownloadUrl(it)
                onFinish(result.data)
            } ?: run {
                onFinish(null)
            }
        }
    }
}