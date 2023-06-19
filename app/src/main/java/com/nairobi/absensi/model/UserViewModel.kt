package com.nairobi.absensi.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nairobi.absensi.data.User
import com.nairobi.absensi.neural.FaceDetector
import com.nairobi.absensi.repo.AttendanceRepository
import com.nairobi.absensi.repo.LeaveRepository
import com.nairobi.absensi.repo.OvertimeRepository
import com.nairobi.absensi.repo.StorageRepository
import com.nairobi.absensi.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository,
    private val attendanceRepository: AttendanceRepository,
    private val overtimeRepository: OvertimeRepository,
    private val leaveRepository: LeaveRepository,
) : ViewModel() {
    val user: MutableState<User?> = mutableStateOf(null)
    val userPhoto: MutableState<Bitmap?> = mutableStateOf(null)
    val users: MutableState<List<User>> = mutableStateOf(emptyList())
    val error: MutableState<String?> = mutableStateOf(null)
    val loading: MutableState<Boolean> = mutableStateOf(false)
    val tempUser: MutableState<User?> = mutableStateOf(null)

    fun getUser(id: String, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = userRepository.getUserBy("id", id)
            user.value = result.data
            user.value?.photo?.let {
                val res = storageRepository.getBitmap(it)
                userPhoto.value = res.data
            }
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun getUserByEmail(email: String, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = userRepository.getUserBy("email", email)
            user.value = result.data
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

    fun refreshUsers() {
        viewModelScope.launch {
            if (users.value.size != userRepository.countUsers()) {
                getUsers()
            }
        }
    }

    fun clean() {
        users.value = emptyList()
        error.value = null
        loading.value = false
        userPhoto.value = null
        tempUser.value = null
    }

    fun getUsersByRole(role: String, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = userRepository.getUsersWhere("role", role)
            users.value = result.data ?: emptyList()
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun addUser(context: Context, user: User, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = userRepository.addUser(user)
            if (result.error == null && user.face != null) {
                val face = FaceDetector().detectFromUri(context, user.face!!)
                face?.let { f ->
                    val upres = storageRepository.uploadBitmap("faces/${user.id}.jpg", f)
                    if (upres.error == null) {
                        user.face = upres.data!!
                        userRepository.updateUser(user)
                    }
                }
            }
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun updateUser(context: Context, user: User, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = userRepository.updateUser(user)
            if (result.error == null) {
                if (user.face != "faces/${user.id}.jpg") {
                    if (user.face == null) {
                        storageRepository.deleteFile("faces/${user.id}.jpg")
                    } else {
                        val face = FaceDetector().detectFromUri(context, user.face!!)
                        face?.let { f ->
                            val upres = storageRepository.uploadBitmap("faces/${user.id}.jpg", f)
                            if (upres.error == null) {
                                user.face = upres.data!!
                                userRepository.updateUser(user)
                            }
                        }
                    }
                }
                if (user.photo != "photos/${user.id}.jpg") {
                    if (user.photo == null) {
                        storageRepository.deleteFile("photos/${user.id}.jpg")
                    } else {
                        val upres = storageRepository.uploadFile(
                            "photos/${user.id}.jpg",
                            Uri.parse(user.photo!!)
                        )
                        if (upres.error == null) {
                            user.photo = upres.data!!
                            userRepository.updateUser(user)
                        }
                    }
                }
            }
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }

    fun deleteUser(id: String, onFinish: () -> Unit = {}) {
        viewModelScope.launch {
            loading.value = true
            val result = userRepository.deleteUser(id)
            if (result.error == null) {
                storageRepository.deleteFile("faces/$id.jpg")
                storageRepository.deleteFile("photos/$id.jpg")
                attendanceRepository.deleteAttendanceWhere("userId", id)
                overtimeRepository.deleteOvertimeWhere("userId", id)
                val leaves = leaveRepository.getLeavesWhere("userId", id)
                leaves.data?.forEach { leave ->
                    storageRepository.deleteFile("documents/${leave.id}")
                }
                leaveRepository.deleteLeaveWhere("userId", id)
            }
            error.value = result.error
            loading.value = false
            onFinish()
        }
    }
}