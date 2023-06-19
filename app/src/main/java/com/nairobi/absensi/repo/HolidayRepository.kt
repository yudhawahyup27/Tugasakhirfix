package com.nairobi.absensi.repo

import com.nairobi.absensi.data.Result
import org.json.JSONArray
import java.net.URL
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HolidayRepository @Inject constructor(
    private val holidayApi: String
) {
    suspend fun getHolidays(): Result<List<Date>, String?> {
        try {
            val url = URL(holidayApi)
            val response = url.readText()
            val data = JSONArray(response)
            val holidays = mutableListOf<Date>()
            for (i in 0 until data.length()) {
                val obj = data.getJSONObject(i)
                val dateSplit = obj.getString("holiday_date").split("-")
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, dateSplit[0].toInt())
                cal.set(Calendar.MONTH, dateSplit[1].toInt() - 1)
                cal.set(Calendar.DAY_OF_MONTH, dateSplit[2].toInt())
                holidays.add(cal.time)
            }
            return Result(holidays)
        } catch (e: Exception) {
            return Result(null, e.message)
        }
    }
}