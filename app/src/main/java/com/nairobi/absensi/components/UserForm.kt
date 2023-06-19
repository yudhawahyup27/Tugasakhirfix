package com.nairobi.absensi.components

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.GeoPoint
import com.nairobi.absensi.model.UserViewModel
import com.nairobi.absensi.utils.datePicker
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.getAddress

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserForm(navController: NavController, userViewModel: UserViewModel, isAdd: Boolean = false) {
    if (userViewModel.tempUser.value == null) {
        navController.popBackStack()
    }

    val context = LocalContext.current
    val user = userViewModel.tempUser.value!!
    val leadingTitle = if (isAdd) "Tambah" else "Edit"
    val trailingTitle = if (user.role == "ADMIN") "Admin" else "Karyawan"

    var name by remember { mutableStateOf(user.name) }
    var nameError by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf(user.email) }
    var emailError by remember { mutableStateOf(false) }
    var nip by remember { mutableStateOf(user.nip ?: "") }
    var nipError by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var phoneError by remember { mutableStateOf(false) }
//    var address by remember { mutableStateOf(user.address) }
//    var addressString by remember { mutableStateOf("") }
//    var addressError by remember { mutableStateOf(false) }
    var dob by remember { mutableStateOf(user.dob) }
    var dobError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf(user.password) }
    var face by remember { mutableStateOf(user.face ?: "") }
    var faceError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var emailAllowed by remember { mutableStateOf(false) }
    var nipAllowed by remember { mutableStateOf(false) }
    var jabatan by remember { mutableStateOf(user.jabatan?:"") }
    var jabatanError by remember { mutableStateOf(false) }


//    val mapPickLauncher = MapPickLauncher(context as Activity) { lat, long ->
//        val geoPoint = GeoPoint(lat, long)
//        address = geoPoint
//        getAddress(context, lat, long) {
//            addressString = it
//        }
//    }

    val imagePickLauncher = ImagePickLauncher(context  as Activity) {
        if (it.isNotEmpty()) {
            face = it
        }
    }

//    if (address != null) {
//        getAddress(context, address!!.latitude, address!!.longitude) {
//            addressString = it
//        }
//    }

    val formattedDate = {
        if (dob != null) {
            formatDate(dob!!, "dd MMMM yyyy")
        } else {
            ""
        }
    }

    fun empty(): Boolean {
        return if (user.role == "KARYAWAN") {
            name.isEmpty() || email.isEmpty() || nip.isEmpty() || phone.isEmpty() || jabatan.isEmpty() ||   dob == null || password.isEmpty() || face.isEmpty()
        } else {
            name.isEmpty() || email.isEmpty() || password.isEmpty()
        }
    }

    fun save() {
        if (nameError || emailError || passwordError || user.role == "KARYAWAN" && (
                    nipError || phoneError || dobError || faceError || jabatanError || empty()
                    )
        ) {
            errorAlert(
                context,
                "$leadingTitle $trailingTitle gagal",
                "Pastikan semua data sudah terisi dengan benar",
            )
        } else if (user.email != email && !emailAllowed) {
            val loading = loadingAlert(context)
            val conflict = userViewModel.users.value.find { it.email == email }
            if (conflict == null) {
                loading.dismissWithAnimation()
                emailAllowed = true
                save()
            } else {
                loading.dismissWithAnimation()
                errorAlert(
                    context,
                    "$leadingTitle $trailingTitle gagal",
                    "Email sudah digunakan",
                )
            }
        } else if (user.role == "KARYAWAN" && user.nip != nip && !nipAllowed) {
            val loading = loadingAlert(context)
            val conflict = userViewModel.users.value.find { it.nip == nip }
            if (conflict == null) {
                loading.dismissWithAnimation()
                nipAllowed = true
                save()
            } else {
                loading.dismissWithAnimation()
                errorAlert(
                    context,
                    "$leadingTitle $trailingTitle gagal",
                    "NIP sudah digunakan",
                )
            }
        } else {
            val loading = loadingAlert(context)
            user.email = email
            user.name = name
            user.password = password
            if (user.role == "KARYAWAN") {
                user.nip = nip
                user.phone = phone
                user.dob = dob
                user.face = face
                user.jabatan = jabatan
            }
            if (isAdd) {
                userViewModel.addUser(context, user) {
                    loading.dismissWithAnimation()
                    successAlert(
                        context,
                        "$leadingTitle $trailingTitle berhasil",
                        "Data $trailingTitle berhasil ditambahkan",
                    ) {
                        navController.popBackStack()
                    }
                }
            } else {
                userViewModel.updateUser(context, user) {
                    loading.dismissWithAnimation()
                    successAlert(
                        context,
                        "$leadingTitle $trailingTitle berhasil",
                        "Data $trailingTitle berhasil diubah",
                    ) {
                        userViewModel.getUsers()
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBar(navController, "$leadingTitle $trailingTitle")
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .weight(4f)
        ) {
            FormText(
                text = name,
                label = "Nama",
                onTextChange = { name = it },
                onTextErrorChange = { nameError = it },
                leadingIcon = {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = "Nama",
                    )
                },
                validator = { !it.isBlank() },
                textError = nameError,
                supportingText = "Nama tidak boleh kosong",
                modifier = Modifier.fillMaxWidth()
            )
            FormText(
                text = jabatan,
                label = "Jabatan",
                onTextChange = { jabatan = it },
                onTextErrorChange = { jabatanError = it },
                leadingIcon = {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = "jabatan",
                    )
                },
                validator = { !it.isBlank() },
                textError = jabatanError,
                supportingText = "Jabatantidak boleh kosong",
                modifier = Modifier.fillMaxWidth()
            )
            FormEmail(
                email = email,
                onEmailChange = { email = it },
                onEmailErrorChange = { emailError = it },
                emailError = emailError,
                modifier = Modifier.fillMaxWidth()
            )
            if (user.role == "KARYAWAN") {
                FormText(
                    text = nip,
                    label = "NIP",
                    leadingIcon = {
                        Icon(
                            Icons.Default.Key,
                            contentDescription = "NIP",
                        )
                    },
                    onTextChange = { nip = it },
                    onTextErrorChange = { nipError = it },
                    validator = { it.length == 6 },
                    textError = nipError,
                    supportingText = "NIP harus 6 digit",
                    isNumber = true,
                    modifier = Modifier.fillMaxWidth()
                )
                FormText(
                    text = phone,
                    label = "Nomor Telepon",
                    leadingIcon = {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Nomor Telepon",
                        )
                    },
                    onTextChange = { phone = it },
                    onTextErrorChange = { phoneError = it },
                    validator = { it.length in 11..13 },
                    textError = phoneError,
                    supportingText = "Nomor Telepon harus 11-13 digit",
                    isNumber = true,
                    modifier = Modifier.fillMaxWidth()
                )

                FormText(
                    text = formattedDate(),
                    label = "Tanggal Lahir",
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Tanggal Lahir",
                        )
                    },
                    onTextChange = { },
                    onTextErrorChange = { dobError = it },
                    validator = { dob != null },
                    textError = dobError,
                    supportingText = "Tanggal Lahir tidak boleh kosong",
                    disabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePicker(context) { date ->
                                dob = date
                            }
                        }
                )
                FormText(
                    text = face,
                    label = "Wajah",
                    leadingIcon = {
                        Icon(
                            Icons.Default.Face,
                            contentDescription = "Wajah",
                        )
                    },
                    onTextChange = {},
                    disabled = true,
                    wrap = true,
                    onTextErrorChange = { faceError = it },
                    validator = { it.isNotBlank() },
                    textError = faceError,
                    supportingText = "Wajah tidak boleh kosong",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            imagePickLauncher()
                        }
                )
            } else {
                FormPassword(
                    password = password,
                    onPasswordChange = { password = it },
                    onPasswordErrorChange = { passwordError = it },
                    passwordError = passwordError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f),
        ) {
            Button(
                onClick = { save() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White,
                    containerColor = Purple,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Simpan")
            }
            if (userViewModel.user.value?.email != user.email && !isAdd) {
                Button(
                    onClick = {
                        warningAlert(
                            context,
                            "Hapus $trailingTitle",
                            "Apakah anda yakin ingin menghapus $trailingTitle ini?",
                            positiveText = "Ya",
                            negativeText = "Tidak",
                            positiveAction = {
                                val loading = loadingAlert(context)
                                userViewModel.deleteUser(user.id) {
                                    loading.dismissWithAnimation()
                                    successAlert(
                                        context,
                                        "Hapus $trailingTitle berhasil",
                                        "Data $trailingTitle berhasil dihapus",
                                    ) {
                                        navController.popBackStack()
                                    }
                                }
                            },
                        )
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Red,
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Hapus")
                }
            }
        }
    }
}