package com.nairobi.absensi.repo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.nairobi.absensi.data.Leave
import com.nairobi.absensi.data.Result
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LeaveRepository @Inject constructor(
    @Named("leaveCollection")
    private val leaveCollection: CollectionReference
) {
    suspend fun getLeaves(): Result<List<Leave>, String?> {
        return try {
            val leaves = leaveCollection.get().await().toObjects(Leave::class.java)
            Result(leaves)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun getLeavesWhere(field: String, value: String): Result<List<Leave>, String?> {
        return try {
            val leaves =
                leaveCollection.whereEqualTo(field, value).get().await()
                    .toObjects(Leave::class.java)
            Result(leaves)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun addLeave(leave: Leave): Result<String, String?> {
        return try {
            val doc = leaveCollection.document()
            leave.id = doc.id
            doc.set(leave).await()
            Result(leave.id)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun updateLeave(leave: Leave): Result<String, String?> {
        return try {
            leaveCollection.whereEqualTo("id", leave.id).get()
                .await().documents.first().reference.set(
                leave
            ).await()
            Result(leave.id)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun deleteLeave(id: String): Result<String, String?> {
        return try {
            leaveCollection.whereEqualTo("id", id).get()
                .await().documents.first().reference.delete()
                .await()
            Result(id)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun deleteLeaveWhere(field: String, value: String): Result<String, String?> {
        return try {
            val leaves = leaveCollection.whereEqualTo(field, value).get().await()
                .toObjects(Leave::class.java)
            leaves.forEach {
                leaveCollection.document(it.id).delete().await()
            }
            Result(value)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun countLeaves(): Int {
        return try {
            leaveCollection.get().await().size()
        } catch (e: FirebaseFirestoreException) {
            0
        }
    }

    suspend fun countLeavesWhere(field: String, value: String): Int {
        return try {
            leaveCollection.whereEqualTo(field, value).get().await().size()
        } catch (e: FirebaseFirestoreException) {
            0
        }
    }
}