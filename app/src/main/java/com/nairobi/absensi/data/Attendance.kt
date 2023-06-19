package com.nairobi.absensi.data

import java.util.Date

data class Attendance(
    var id: String = "",
    var userId: String = "",
    var date: Date = Date(),
    var checkIn: Date = Date(),
    var checkOut: Date = Date(),
    var status: String = "CHECKIN",
)