package com.nairobi.absensi.repo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.nairobi.absensi.data.EarlyCheckout
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import com.nairobi.absensi.data.Result
import kotlinx.coroutines.tasks.await

@Singleton
class EarlyCheckoutRepository @Inject constructor(
    @Named("earlyCheckoutCollection")
    private val earlyCheckoutCollection: CollectionReference
) {
    suspend fun getEarlyCheckouts(): Result<List<EarlyCheckout>, String?> {
        return try {
            val earlyCheckouts = earlyCheckoutCollection.get().await().toObjects(EarlyCheckout::class.java)
            Result(earlyCheckouts)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun getEarlyCheckoutsWhere(
        field: String,
        value: String
    ): Result<List<EarlyCheckout>, String?> {
        return try {
            val earlyCheckouts = earlyCheckoutCollection.whereEqualTo(field, value).get().await()
                .toObjects(EarlyCheckout::class.java)
            Result(earlyCheckouts)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun addEarlyCheckout(earlyCheckout: EarlyCheckout): Result<String, String?> {
        return try {
            val doc = earlyCheckoutCollection.document()
            earlyCheckout.id = doc.id
            doc.set(earlyCheckout).await()
            Result(doc.id)
        } catch (e: FirebaseFirestoreException) {
            return Result(null, e.message)
        }
    }

    suspend fun updateEarlyCheckout(earlyCheckout: EarlyCheckout): Result<Boolean, String?> {
        return try {
            earlyCheckoutCollection.whereEqualTo("id", earlyCheckout.id).get().await().documents.first()
                .reference.set(earlyCheckout).await()
            Result(true)
        } catch (e: FirebaseFirestoreException) {
            Result(false, e.message)
        }
    }

    suspend fun deleteEarlyCheckout(earlyCheckout: EarlyCheckout): Result<Boolean, String?> {
        return try {
            earlyCheckoutCollection.whereEqualTo("id", earlyCheckout.id).get().await().documents.first()
                .reference.delete().await()
            Result(true)
        } catch (e: FirebaseFirestoreException) {
            Result(false, e.message)
        }
    }

    suspend fun deleteEarlyCheckoutWhere(
        field: String,
        value: String
    ): Result<Boolean, String?> {
        return try {
            earlyCheckoutCollection.whereEqualTo(field, value).get().await().documents.first()
                .reference.delete().await()
            Result(true)
        } catch (e: FirebaseFirestoreException) {
            Result(false, e.message)
        }
    }

    suspend fun countEarlyCheckout(): Int {
        return try {
            earlyCheckoutCollection.get().await().toObjects(EarlyCheckout::class.java).size
        } catch (e: FirebaseFirestoreException) {
            0
        }
    }

    suspend fun countEarlyCheckoutWhere(
        field: String,
        value: String
    ): Int {
        return try {
            earlyCheckoutCollection.whereEqualTo(field, value).get().await().toObjects(EarlyCheckout::class.java).size
        } catch (e: FirebaseFirestoreException) {
            0
        }
    }
}