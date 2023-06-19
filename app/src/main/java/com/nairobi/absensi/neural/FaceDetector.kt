package com.nairobi.absensi.neural

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.coroutines.tasks.await

class FaceDetector {
    private val detector: FirebaseVisionFaceDetector
    private var error: String? = null

    init {
        val opts = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
            .build()
        detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(opts)
    }

    suspend fun detect(image: Bitmap): Bitmap? {
        try {
            val result = detector.detectInImage(FirebaseVisionImage.fromBitmap(image)).await()
            if (result.isEmpty()) {
                return null
            }
            val face = result[0]
            val left = face.boundingBox.left
            val top = face.boundingBox.top
            val right = face.boundingBox.right
            val bottom = face.boundingBox.bottom
            val bm = Bitmap.createBitmap(image, left, top, right - left, bottom - top)
            return Bitmap.createScaledBitmap(bm, 112, 112, false)
        } catch (e: Exception) {
            error = e.message
            return null
        }
    }

    fun getError(): String? {
        return error
    }

    suspend fun detectFromUri(context: Context, uri: String): Bitmap? {
        val bitmap = MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            Uri.parse(uri)
        )
        return detect(bitmap)
    }
}