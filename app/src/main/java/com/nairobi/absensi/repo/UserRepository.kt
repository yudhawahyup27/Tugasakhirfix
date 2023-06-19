package com.nairobi.absensi.repo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.nairobi.absensi.data.Result
import com.nairobi.absensi.data.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @Named("userCollection")
    private val userCollection: CollectionReference,
) {
    suspend fun getUserBy(field: String, value: String): Result<User, String?> {
        return try {
            val user =
                userCollection.whereEqualTo(field, value).get().await().toObjects(User::class.java)
                    .first()
            Result(user)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun getUsers(): Result<List<User>, String?> {
        return try {
            val users = userCollection.get().await().toObjects(User::class.java)
            Result(users)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun getUsersWhere(field: String, value: String): Result<List<User>, String?> {
        return try {
            val users =
                userCollection.whereEqualTo(field, value).get().await().toObjects(User::class.java)
            Result(users)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun addUser(user: User): Result<String, String?> {
        return try {
            val doc = userCollection.document()
            user.id = doc.id
            doc.set(user).await()
            Result(user.id)
        } catch (e: FirebaseFirestoreException) {
            Result(null, e.message)
        }
    }

    suspend fun updateUser(user: User): Result<Boolean, String?> {
        return try {
            userCollection.whereEqualTo("id", user.id).get()
                .await().documents.first().reference.set(
                user
            ).await()
            Result(true)
        } catch (e: FirebaseFirestoreException) {
            Result(false, e.message)
        }
    }

    suspend fun deleteUser(id: String): Result<Boolean, String?> {
        return try {
            userCollection.whereEqualTo("id", id).get().await().documents.first().reference.delete()
                .await()
            Result(true)
        } catch (e: FirebaseFirestoreException) {
            Result(false, e.message)
        }
    }

    suspend fun countUsers(): Int {
        return try {
            userCollection.get().await().size()
        } catch (e: FirebaseFirestoreException) {
            0
        }
    }
}