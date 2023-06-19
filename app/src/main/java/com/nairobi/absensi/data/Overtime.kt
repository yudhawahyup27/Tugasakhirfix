package com.nairobi.absensi.data

import java.util.Date

data class Overtime(
    var startTime: Date = Date(),
    var endTime: Date = Date(),
    var reason: String = "",
    var status: String = "PENDING",
    var id: String = "",
    var userId: String = "",
)