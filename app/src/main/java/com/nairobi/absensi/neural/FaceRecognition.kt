package com.nairobi.absensi.neural

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import kotlin.math.sqrt

class FaceRecognition(context: Context, private val inputSize: Int) {
    companion object {
        private const val OUTPUT_SIZE = 192
        private const val NUM_DETECTIONS = 1
        private const val IMAGE_MEAN = 128.0f
        private const val IMAGE_STD = 128.0f
    }

    private var interpreter: Interpreter
    private var imgData: ByteBuffer
    private var intValues: IntArray
    private var outputLocations: ArrayList<Array<FloatArray>>
    private var outputClasses: ArrayList<FloatArray>
    private var outputScores: ArrayList<FloatArray>
    private var numDetections: FloatArray
    private var embeedings: Array<FloatArray> = arrayOf()
    private val registered: HashMap<String, Recognition> = HashMap()

    fun register(name: String, rec: Recognition) {
        registered[name] = rec
    }

    init {
        val opts = Interpreter.Options().apply {
            if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                addDelegate(GpuDelegate(CompatibilityList().bestOptionsForThisDevice))
            }
            useXNNPACK = true
            useNNAPI = true
        }
        interpreter = Interpreter(FileUtil.loadMappedFile(context, "rd_face.tflite"), opts)
        imgData = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4)
        imgData.order(java.nio.ByteOrder.nativeOrder())
        intValues = IntArray(inputSize * inputSize)
        outputLocations = ArrayList(1)
        outputLocations.add(Array(NUM_DETECTIONS) { FloatArray(4) })
        outputClasses = ArrayList(1)
        outputClasses.add(FloatArray(NUM_DETECTIONS))
        outputScores = ArrayList(1)
        outputScores.add(FloatArray(NUM_DETECTIONS))
        numDetections = FloatArray(1)
    }

    private fun findNearest(emb: FloatArray): Pair<String, Float>? {
        var res: Pair<String, Float>? = null
        registered.entries.forEach { entry ->
            val name = entry.key
            val knownEmb = entry.value.extra[0]
            var distance = 0f
            for (i in knownEmb.indices) {
                val diff = emb[i] - knownEmb[i]
                distance += diff * diff
            }
            distance = sqrt(distance)
            if (res == null || distance < res!!.second) {
                res = Pair(name, distance)
            }
        }

        return res
    }

    fun recognize(bitmap: Bitmap): ArrayList<Recognition> {
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        imgData.rewind()

        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]
                imgData.putFloat((((pixelValue shr 16) and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat((((pixelValue shr 8) and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }

        val inputArray = arrayOf(imgData)
        val outputMap = HashMap<Int, Any>()
        embeedings = Array(1) { FloatArray(OUTPUT_SIZE) }
        outputMap[0] = embeedings

        interpreter.runForMultipleInputsOutputs(inputArray, outputMap)

        var distance = Float.MAX_VALUE
        val id = "0"
        var label = "Unknown"

        if (registered.size > 0) {
            val nearest = findNearest(embeedings[0])
            if (nearest != null) {
                label = nearest.first
                distance = nearest.second
            }
        }

        val recognitions = ArrayList<Recognition>(1)
        val rec = Recognition(id, label, distance, embeedings)
        recognitions.add(rec)

        return recognitions
    }
}