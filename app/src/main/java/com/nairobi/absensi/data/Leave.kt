package com.nairobi.absensi.data

import java.util.Date

data class Leave(
    var id: String = "",
    var userId: String = "",
    var start: Date = Date(),
    var end: Date = Date(),
    var reason: String = "",
    var status: String = "PENDING",
    var document: String? = null
)