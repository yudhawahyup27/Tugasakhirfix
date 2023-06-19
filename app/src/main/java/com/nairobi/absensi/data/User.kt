package com.nairobi.absensi.data

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var nip: String? = null,
    var phone: String? = null,
    var address: GeoPoint? = null,
    var role: String = "KARYAWAN",
    var password: String = "123456",
    var dob: Date? = null,
    var face: String? = null,
    var photo: String? = null,
    var jabatan: String? = null,
)