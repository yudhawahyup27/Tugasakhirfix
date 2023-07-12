package com.nairobi.absensi.data

import java.util.Date

data class EarlyCheckout(
    var id: String = "",
    var userId: String = "",
    var date: Date = Date(),
    var status: String = "PENDING"
)