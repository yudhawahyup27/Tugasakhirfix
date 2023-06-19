package com.nairobi.absensi.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun launchBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}