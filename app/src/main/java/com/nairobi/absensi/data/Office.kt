package com.nairobi.absensi.data

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Office(
    var openTime: Date = Date(),
    var closeTime: Date = Date(),
    var address: GeoPoint = GeoPoint(0.0, 0.0),
)