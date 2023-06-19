package com.nairobi.absensi.dashboard.admin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.dashboard.DashboardActivity
import com.nairobi.absensi.model.AttendanceViewModel
import com.nairobi.absensi.model.ExportViewModel
import com.nairobi.absensi.model.LeaveViewModel
import com.nairobi.absensi.model.OfficeViewModel
import com.nairobi.absensi.model.OvertimeViewModel
import com.nairobi.absensi.model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardAdminActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("userId")
        if (userId == null) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            dashboardAdminRouter(userId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dashboardAdminRouter(userId: String) {
        setContent {
            val navController = rememberNavController()
            val userViewModel: UserViewModel = hiltViewModel()
            val officeViewModel: OfficeViewModel = hiltViewModel()
            val attendanceViewModel: AttendanceViewModel = hiltViewModel()
            val overtimeViewModel: OvertimeViewModel = hiltViewModel()
            val leaveViewModel: LeaveViewModel = hiltViewModel()
            val exportViewModel: ExportViewModel = hiltViewModel()

            if (userViewModel.user.value == null) {
                userViewModel.getUser(userId) {
                    val user = userViewModel.user.value
                    if (user == null) {
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (user.role != "ADMIN") {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                        finish()
                    }
                }
                ProgressScreen()
            } else {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        Home(navController)
                    }
                    composable("admin") {
                        Admin(navController, userViewModel)
                    }
                    composable("edit") {
                        Edit(navController, userViewModel)
                    }
                    composable("add") {
                        Add(navController, userViewModel)
                    }
                    composable("user") {
                        Officer(navController, userViewModel)
                    }
                    composable("office") {
                        Office(navController, officeViewModel)
                    }
                    composable("overtime") {
                        Overtime(navController, overtimeViewModel)
                    }
                    composable("overtime/add") {
                        AddOvertime(navController, overtimeViewModel)
                    }
                    composable("leave") {
                        Leave(navController, leaveViewModel)
                    }
                    composable("leave/detail") {
                        DetailLeave(navController, leaveViewModel)
                    }
                    composable("attendance") {
                        Attendance(navController, attendanceViewModel)
                    }
                    composable("exports") {
                        Exports(navController, exportViewModel)
                    }
                }
            }
        }
    }
}