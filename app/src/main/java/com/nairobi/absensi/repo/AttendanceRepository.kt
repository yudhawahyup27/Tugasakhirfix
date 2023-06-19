package com.nairobi.absensi.repo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.nairobi.absensi.data.Attendance
import com.nairobi.absensi.data.Result
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    @Named("attendanceCollection")
    private val attendanceCollection: CollectionReference
) {
    suspend fun getAttendances(): Result<List<Attendance>, String?> {
        return try {
            val attendances = attendanceCollection.get().await().toObjects(Attendance::class.java)
            Result(attendances)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun getAttendancesWhere(
        field: String,
        value: String
    ): Result<List<Attendance>, String?> {
        return try {
            val attendances = attendanceCollection.whereEqualTo(field, value).get().await()
                .toObjects(Attendance::class.java)
            Result(attendances)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun addAttendance(attendance: Attendance): Result<String, String?> {
        return try {
            val doc = attendanceCollection.document()
            attendance.id = doc.id
            doc.set(attendance).await()
            Result(doc.id)
        } catch (e: FirebaseFirestoreException) {
            return Result(null, e.message)
        }
    }

    suspend fun updateAttendance(attendance: Attendance): Result<Boolean, String?> {
        return try {
            attendanceCollection.whereEqualTo("id", attendance.id).get().await().documents.first()
                .reference.set(attendance).await()
            Result(true)
        } catch (e: FirebaseFirestoreException) {
            Result(false, e.message)
        }
    }

    suspend fun deleteAttendance(attendance: Attendance): Result<Boolean, String?> {
        return try {
            attendanceCollection.whereEqualTo("id", attendance.id).get().await().documents.first()
                .reference.delete().await()
            Result(true)
        } catch (e: FirebaseFirestoreException) {
            Result(false, e.message)
        }
    }

    suspend fun deleteAttendanceWhere(field: String, value: String): Result<Boolean, String?> {
        return try {
            val attendances = attendanceCollection.whereEqualTo(field, value).get().await()
                .toObjects(Attendance::class.java)
            attendances.forEach {
                attendanceCollection.document(it.id).delete().await()
            }
            Result(true)
        } catch (e: FirebaseFirestoreException) {
            Result(false, e.message)
        }
    }

    suspend fun countAttendances(): Int {
        return try {
            val attendances = attendanceCollection.get().await().toObjects(Attendance::class.java)
            attendances.size
        } catch (e: FirebaseFirestoreException) {
            0
        }
    }

    suspend fun countAttendancesWhere(field: String, value: String): Int {
        return try {
            val attendances = attendanceCollection.whereEqualTo(field, value).get().await()
                .toObjects(Attendance::class.java)
            attendances.size
        } catch (e: FirebaseFirestoreException) {
            0
        }
    }
}