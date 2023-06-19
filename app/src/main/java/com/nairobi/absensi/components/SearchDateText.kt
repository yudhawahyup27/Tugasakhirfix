package com.nairobi.absensi.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.nairobi.absensi.utils.datePicker
import com.nairobi.absensi.utils.formatDate
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchDateText(
    date: Date?,
    onDateChange: (Date?) -> Unit,
    text: String,
    onTextChange: (String) -> Unit
) {
    val context = LocalContext.current
    FormText(
        text = date?.let { formatDate(it, "dd MMMM yyyy") } ?: "",
        label = "Tanggal",
        disabled = true,
        leadingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = null)
        },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onDateChange(null)
                    }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                datePicker(context) {
                    onDateChange(it)
                }
            }
    )
    FormText(
        text = text,
        label = "Cari",
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onTextChange("")
                    }
            )
        },
        onTextChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
    )
}