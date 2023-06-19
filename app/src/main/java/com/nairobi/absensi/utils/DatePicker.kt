package com.nairobi.absensi.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.Calendar
import java.util.Date

fun datePicker(context: Context, onFinish: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val date = Calendar.getInstance()
            date.set(year, month, dayOfMonth)
            onFinish(date.time)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun timePicker(context: Context, onFinish: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val date = Calendar.getInstance()
            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
            date.set(Calendar.MINUTE, minute)
            onFinish(date.time)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        true
    ).show()
}

fun dateTimePicker(context: Context, onFinish: (Date) -> Unit) {
    datePicker(context) { date ->
        timePicker(context) { time ->
            val cal = Calendar.getInstance()
            cal.time = date
            val hour = Calendar.getInstance()
            hour.time = time
            cal.set(Calendar.HOUR_OF_DAY, hour.get(Calendar.HOUR_OF_DAY))
            cal.set(Calendar.MINUTE, hour.get(Calendar.MINUTE))
            onFinish(cal.time)
        }
    }
}