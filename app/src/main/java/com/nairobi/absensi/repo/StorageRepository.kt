package com.nairobi.absensi.repo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.nairobi.absensi.data.Result
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(
    private val storage: StorageReference,
) {
    private suspend fun uploadBytes(path: String, bytes: ByteArray): Result<String, String?> {
        return try {
            val ref = storage.child(path)
            ref.putBytes(bytes).await()
            Result(path)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun uploadBitmap(path: String, bm: Bitmap): Result<String, String?> {
        return try {
            val boas = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, boas)
            val bytes = boas.toByteArray()
            uploadBytes(path, bytes)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun uploadFile(path: String, uri: Uri): Result<String, String?> {
        return try {
            val ref = storage.child(path)
            ref.putFile(uri).await()
            Result(path)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun deleteFile(path: String): Result<String, String?> {
        return try {
            val ref = storage.child(path)
            ref.delete().await()
            Result(path)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun uploadExcel(path: String, bytes: ByteArray): Result<String, String?> {
        return try {
            val ref = storage.child(path)
            // set content type to excel
            val metadata = StorageMetadata.Builder()
                .setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .build()
            ref.putBytes(bytes, metadata).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result(downloadUrl)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun getFile(path: String): Result<ByteArray, String?> {
        return try {
            val ref = storage.child(path)
            val bytes = ref.getBytes(1024 * 1024).await()
            Result(bytes)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun getBitmap(path: String): Result<Bitmap, String?> {
        return try {
            val ref = storage.child(path)
            val bytes = ref.getBytes(Long.MAX_VALUE).await()
            val bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Result(bm)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }

    suspend fun getDownloadUrl(path: String): Result<String, String?> {
        return try {
            val ref = storage.child(path)
            val url = ref.downloadUrl.await().toString()
            Result(url)
        } catch (e: Exception) {
            Result(null, e.message)
        }
    }
}