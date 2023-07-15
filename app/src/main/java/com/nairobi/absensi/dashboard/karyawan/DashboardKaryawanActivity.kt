package com.nairobi.absensi.dashboard.karyawan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nairobi.absensi.dashboard.DashboardActivity
import com.nairobi.absensi.model.AttendanceViewModel
import com.nairobi.absensi.model.LeaveViewModel
import com.nairobi.absensi.model.OvertimeViewModel
import com.nairobi.absensi.model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardKaryawanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("userId")
        if (userId == null) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            dashboardKaryawanRouter(userId)
        }
    }

    private fun dashboardKaryawanRouter(userId: String) {
        setContent {
            val navController = rememberNavController()
            val userViewModel: UserViewModel = hiltViewModel()
            val attendanceViewModel: AttendanceViewModel = hiltViewModel()
            val overtimeViewModel: OvertimeViewModel = hiltViewModel()
            val leaveViewModel: LeaveViewModel = hiltViewModel()

            if (userViewModel.user.value == null) {
                userViewModel.getUser(userId) {
                    leaveViewModel.user.value = userViewModel.user.value
                    overtimeViewModel.user.value = userViewModel.user.value
                    attendanceViewModel.user.value = userViewModel.user.value
                }
            }

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    Home(navController, overtimeViewModel, attendanceViewModel)
                }
                composable("leave") {
                    Leave(navController, leaveViewModel)
                }
                composable("leave/add") {
                    AddEditLeave(navController, leaveViewModel)
                }
                composable("leave/edit") {
                    AddEditLeave(navController, leaveViewModel, true)
                }
                composable("account") {
                    Account(navController, userViewModel)
                }
                composable("attendance") {
                    Attendance(navController, attendanceViewModel)
                }
                composable("overtime") {
                    Overtime(navController, overtimeViewModel)
                }
                composable("checkin") {
                    Checkin(navController, attendanceViewModel)
                }
                composable("checkout") {
                    Checkout(navController, attendanceViewModel)
                }
            }
        }
    }
}