package com.nairobi.absensi.repo

import com.google.firebase.firestore.CollectionReference
import com.nairobi.absensi.data.Overtime
import com.nairobi.absensi.data.Result
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class OvertimeRepository @Inject constructor(
    @Named("overtimeCollection")
    private val overtimeCollection: CollectionReference
) {
    suspend fun getOvertimes(): Result<List<Overtime>, String?> {
        return try {
            val overtimes = overtimeCollection.get().await().toObjects(Overtime::class.java)
            Result(overtimes)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun getOvertimesWhere(field: String, value: String): Result<List<Overtime>, String?> {
        return try {
            val overtimes =
                overtimeCollection.whereEqualTo(field, value).get().await()
                    .toObjects(Overtime::class.java)
            Result(overtimes)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun addOvertime(overtime: Overtime): Result<String, String?> {
        return try {
            val doc = overtimeCollection.document()
            overtime.id = doc.id
            doc.set(overtime).await()
            Result(overtime.id)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun updateOvertime(overtime: Overtime): Result<String, String?> {
        return try {
            overtimeCollection.whereEqualTo("id", overtime.id).get()
                .await().documents.first().reference.set(
                overtime
            ).await()
            Result(overtime.id)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun deleteOvertime(id: String): Result<String, String?> {
        return try {
            overtimeCollection.whereEqualTo("id", id).get()
                .await().documents.first().reference.delete().await()
            Result(id)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun deleteOvertimeWhere(field: String, value: String): Result<String, String?> {
        return try {
            val overtimes =
                overtimeCollection.whereEqualTo(field, value).get().await()
                    .toObjects(Overtime::class.java)
            for (overtime in overtimes) {
                overtimeCollection.document(overtime.id).delete().await()
            }
            Result(value)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun countOvertimes(): Int {
        return try {
            val overtimes = overtimeCollection.get().await().toObjects(Overtime::class.java)
            overtimes.size
        } catch (e: Exception) {
            0
        }
    }

    suspend fun countOvertimesWhere(field: String, value: String): Int {
        return try {
            val overtimes =
                overtimeCollection.whereEqualTo(field, value).get().await()
                    .toObjects(Overtime::class.java)
            overtimes.size
        } catch (e: Exception) {
            0
        }
    }
}