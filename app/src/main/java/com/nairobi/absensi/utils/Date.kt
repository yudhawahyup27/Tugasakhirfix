package com.nairobi.absensi.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(date: Date, format: String): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter)
}

fun isToday(date: Date): Boolean {
    val today = Date()
    return today.date == date.date && today.month == date.month && today.year == date.year
}

fun isBetween(date: Date, start: Date, end: Date): Boolean {
    return date.after(start) && date.before(end)
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    return date1.date == date2.date && date1.month == date2.month && date1.year == date2.year
}

fun getDayDistance(date1: Date, date2: Date): Int {
    val diff = date1.time - date2.time
    return (diff / (1000 * 60 * 60 * 24)).toInt()
}

fun addOneDay(date: Date): Date {
    val calendar = java.util.Calendar.getInstance()
    calendar.time = date
    calendar.add(java.util.Calendar.DATE, 1)
    return calendar.time
}

fun subMinute(date: Date, minute: Int): Date {
    val calendar = java.util.Calendar.getInstance()
    calendar.time = date
    calendar.add(java.util.Calendar.MINUTE, -minute)
    return calendar.time
}

fun getHourDistance(date1: Date, date2: Date): Int {
    val diff = date1.time - date2.time
    return (diff / (1000 * 60 * 60)).toInt()
}

fun getHourMinuteDistance(date1: Date, date2: Date): String {
    if (date1.before(date2)) {
        return "0 jam 0 menit"
    }
    val diff = date1.time - date2.time
    val hours = (diff / (1000 * 60 * 60)).toInt()
    val minutes = (diff / (1000 * 60) % 60).toInt()
    return "$hours jam $minutes menit"
}

fun isSunday(date: Date): Boolean {
    val calendar = java.util.Calendar.getInstance()
    calendar.time = date
    return calendar.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY
}