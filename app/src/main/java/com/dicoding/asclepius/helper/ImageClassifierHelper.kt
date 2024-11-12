package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


class ImageClassifierHelper(
    val treshold: Float = 0.1f,
    val maxResults: Int = 1,
    val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val clasifierListener: ClassifierListener?
) {

    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResult(results: List<Classifications>?)
    }

    private fun setupImageClassifier() {
        val options = ImageClassifier.ImageClassifierOptions.builder().setMaxResults(maxResults)
            .setScoreThreshold(treshold)
        val baseOptions = BaseOptions.builder().setNumThreads(4)
        options.setBaseOptions(baseOptions.build())

        try {
            imageClassifier =
                ImageClassifier.createFromFileAndOptions(context, modelName, options.build())
        } catch (e: IllegalStateException) {
            clasifierListener?.onError(
                "Image classifier failed to initialize. See error logs for details"
            )
            Log.e("ImageClassifierHelper", "TFLite failed to load model with error: " + e.message)

        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        if (imageClassifier == null) {
            setupImageClassifier()
        }
        val imgProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)).add(CastOp(DataType.FLOAT32)).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val src = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(src)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }.copy(Bitmap.Config.ARGB_8888, true)?.let { bitmap ->
            val image = imgProcessor.process(TensorImage.fromBitmap(bitmap))
            val results = imageClassifier?.classify(image)
            clasifierListener?.onResult(results)
        }
    }
}