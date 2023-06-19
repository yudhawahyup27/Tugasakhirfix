package com.nairobi.absensi.components

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun PdfPickerLauncher(
    activity: Activity,
    onResult: (String) -> Unit
): () -> Unit {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data?.toString() ?: ""
            onResult(uri)
        }
    return {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        launcher.launch(intent)
    }
}