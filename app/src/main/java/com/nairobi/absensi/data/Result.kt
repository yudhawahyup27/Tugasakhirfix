package com.nairobi.absensi.data

data class Result<T, E : String?>(
    var data: T? = null,
    var error: E? = null
)