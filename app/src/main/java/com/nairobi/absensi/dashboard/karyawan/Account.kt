package com.nairobi.absensi.dashboard.karyawan

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.GeoPoint
import com.nairobi.absensi.R
import com.nairobi.absensi.components.AppBar
import com.nairobi.absensi.components.ErrorScreen
import com.nairobi.absensi.components.FormEmail
import com.nairobi.absensi.components.FormPassword
import com.nairobi.absensi.components.FormText
import com.nairobi.absensi.components.ImagePickLauncher
import com.nairobi.absensi.components.MapPickLauncher
import com.nairobi.absensi.components.ProgressScreen
import com.nairobi.absensi.components.Purple
import com.nairobi.absensi.components.errorAlert
import com.nairobi.absensi.components.loadingAlert
import com.nairobi.absensi.components.successAlert
import com.nairobi.absensi.components.warningAlert
import com.nairobi.absensi.model.UserViewModel
import com.nairobi.absensi.utils.datePicker
import com.nairobi.absensi.utils.formatDate
import com.nairobi.absensi.utils.getAddress
import com.nairobi.absensi.utils.loadImageFromUri

@Composable
fun Account(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current

    if (userViewModel.loading.value) {
        ProgressScreen()
    } else if (userViewModel.error.value != null) {
        ErrorScreen(userViewModel.error.value!!)
    } else {
        val user = userViewModel.user.value!!
        var name by remember { mutableStateOf(user.name) }
        var nameError by remember { mutableStateOf(false) }
        var email by remember { mutableStateOf(user.email) }
        var emailError by remember { mutableStateOf(false) }
        var phone by remember { mutableStateOf(user.phone ?: "") }
        var phoneError by remember { mutableStateOf(false) }
        val nip by remember { mutableStateOf(user.nip ?: "") }
        var dob by remember { mutableStateOf(user.dob) }
        var dobError by remember { mutableStateOf(false) }
        var address by remember { mutableStateOf(user.address) }
        var addressError by remember { mutableStateOf(false) }
        var addressString by remember { mutableStateOf("") }
        var password by remember { mutableStateOf(user.password) }
        var passwordError by remember { mutableStateOf(false) }
        var photo by remember { mutableStateOf(user.photo) }
        var photoPaint by remember { mutableStateOf<Painter?>(null) }

        if (address != null && addressString.isEmpty()) {
            getAddress(context as Activity, address!!.latitude, address!!.longitude) {
                addressString = it
            }
        }

        if (photo != null && photoPaint == null) {
            photoPaint = BitmapPainter(userViewModel.userPhoto.value!!.asImageBitmap())
        }

        val imagePickerLauncher = ImagePickLauncher(context as Activity) {
            photo = it
            val bm = loadImageFromUri(context, it)
            photoPaint = BitmapPainter(bm)
        }
        val mapPickLauncer = MapPickLauncher(context) { lat, lng ->
            address = GeoPoint(lat, lng)
            getAddress(context, lat, lng) { addressString = it }
        }

        fun save() {
            var valid = true
            if (name.isEmpty()) {
                nameError = true
                valid = false
            }
            if (email.isEmpty()) {
                emailError = true
                valid = false
            }
            if (phone.isEmpty()) {
                phoneError = true
                valid = false
            }
            if (dob == null) {
                dobError = true
                valid = false
            }
            if (address == null) {
                addressError = true
                valid = false
            }
            if (password.isEmpty()) {
                passwordError = true
                valid = false
            }
            if (!valid) {
                errorAlert(
                    context = context,
                    title = "Gagal",
                    text = "Pastikan semua data terisi dengan benar"
                )
            } else {
                val sameMail = userViewModel.users.value.find { it.email == email }
                if (email != user.email && sameMail != null) {
                    errorAlert(
                        context = context,
                        title = "Gagal",
                        text = "Email sudah digunakan"
                    )
                } else {
                    val newUser = user.copy(
                        name = name,
                        email = email,
                        phone = phone,
                        nip = nip,
                        dob = dob!!,
                        address = address!!,
                        password = password,
                        photo = photo
                    )
                    val loading = loadingAlert(context)
                    userViewModel.updateUser(context, newUser) {
                        loading.dismissWithAnimation()
                        userViewModel.getUser(newUser.id)
                        successAlert(
                            context = context,
                            title = "Berhasil",
                            text = "Data berhasil diperbarui"
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AppBar(navController, "Akun Saya")
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    Modifier
                        .width(100.dp)
                        .height(100.dp)
                ) {
                    Image(
                        photoPaint ?: painterResource(R.drawable.default_photo),
                        contentDescription = "Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .clickable {
                                warningAlert(
                                    context = context,
                                    title = "Ubah Foto",
                                    text = "Apakah anda yakin ingin mengubah foto?",
                                    positiveAction = {
                                        imagePickerLauncher()
                                    }
                                )
                            }
                    )
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                FormText(
                    text = name,
                    onTextChange = { name = it },
                    label = "Nama",
                    validator = { it.isNotEmpty() },
                    onTextErrorChange = { nameError = it },
                    supportingText = "Nama tidak boleh kosong",
                    leadingIcon = {
                        Icon(Icons.Default.CreditCard, contentDescription = null)
                    },
                    textError = nameError,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                FormEmail(
                    email = email,
                    onEmailChange = {
                        email = it
                        emailError = false
                    },
                    emailError = emailError,
                    onEmailErrorChange = { emailError = it },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                FormText(
                    text = phone,
                    onTextChange = { phone = it },
                    label = "Nomor Telepon",
                    validator = { it.length in 11..13 },
                    onTextErrorChange = { phoneError = it },
                    supportingText = "Nomor telepon harus 11-13 digit",
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    textError = phoneError,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                FormText(
                    text = nip,
                    label = "NIP",
                    disabled = true,
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                FormText(
                    text = addressString,
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = null)
                    },
                    disabled = true,
                    wrap = true,
                    onTextErrorChange = { addressError = it },
                    textError = addressError,
                    validator = { address != null },
                    supportingText = "Alamat tidak boleh kosong",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val lat = address?.latitude ?: 0.0
                            val lng = address?.longitude ?: 0.0
                            mapPickLauncer(lat, lng)
                        }
                )
                FormText(
                    text = dob?.let { formatDate(it, "dd MMMM yyyy") } ?: "",
                    label = "Tanggal Lahir",
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    disabled = true,
                    wrap = true,
                    onTextErrorChange = { dobError = it },
                    textError = dobError,
                    validator = { dob != null },
                    supportingText = "Tanggal lahir tidak boleh kosong",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePicker(context) { date ->
                                dob = date
                            }
                        }
                )
                FormPassword(
                    password = password,
                    onPasswordChange = { password = it },
                    passwordError = passwordError,
                    onPasswordErrorChange = { passwordError = it },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Button(
                    onClick = { save() },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Purple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(vertical = 50.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Simpan")
                }
            }
        }
    }
}