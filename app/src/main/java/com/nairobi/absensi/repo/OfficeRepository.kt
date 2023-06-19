package com.nairobi.absensi.repo

import com.google.firebase.firestore.CollectionReference
import com.nairobi.absensi.data.Office
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class OfficeRepository @Inject constructor(
        @Named("officeCollection")
        private val officeCollection: CollectionReference,
) {
    suspend fun getOffice(): Office {
        return try {
            val snapshot = officeCollection.get().await()
            if (snapshot.isEmpty) {
                Office()
            } else {
                snapshot.documents[0].toObject(Office::class.java)!!
            }
        } catch (e: Exception) {
            Office()
        }
    }

    suspend fun setOffice(office: Office) {
        val empty = officeCollection.get().await().isEmpty
        if (!empty) {
            officeCollection.get().await().documents[0].reference.set(office).await()
        } else {
            officeCollection.add(office).await()
        }
    }
}