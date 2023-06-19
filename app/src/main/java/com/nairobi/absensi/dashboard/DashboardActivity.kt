package com.nairobi.absensi.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.nairobi.absensi.auth.AuthActivity
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.dashboard.admin.DashboardAdminActivity
import com.nairobi.absensi.dashboard.karyawan.DashboardKaryawanActivity
import com.nairobi.absensi.model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("userId")
        if (userId != null) {
            setRef(userId)
            userViewModel.getUser(userId) {
                if (userViewModel.error.value != null || userViewModel.user.value == null) {
                    errorAlert(
                        this,
                        "Error",
                        userViewModel.error.value ?: "Terjadi kesalahan"
                    ) {
                        deleteRef()
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val user = userViewModel.user.value!!
                    if (user.role == "ADMIN") {
                        val intent = Intent(this, DashboardAdminActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, DashboardKaryawanActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        } else {
            deleteRef()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setRef(email: String) {
        val ref = getSharedPreferences(this.packageName, 0)
        val editor = ref.edit()
        editor.putString("userId", email)
        editor.apply()
    }

    private fun deleteRef() {
        val ref = getSharedPreferences(this.packageName, 0)
        val editor = ref.edit()
        editor.remove("userId")
        editor.apply()
    }
}