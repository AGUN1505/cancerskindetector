package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
 
    private var currentImageUri: Uri? = null
    var resultAnalyzer: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            @Suppress("DEPRECATION")
            currentImageUri = savedInstanceState.getParcelable("current_image_uri")
            showImage()  // Tampilkan gambar jika URI telah dipulihkan
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Simpan URI gambar saat ini ke dalam Bundle untuk mempertahankan gambar
        outState.putParcelable("current_image_uri", currentImageUri)
    }

    private fun startGallery() {
        openGallerry.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI:","showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        if (currentImageUri != null) {
            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                clasifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }
                    override fun onResult(results: List<Classifications>?) {
                        results?.let { it ->
                            println(it)
                            val sortirCategory = it[0].categories.sortedByDescending { it.score }
                            val tampilResult = sortirCategory.joinToString("\n") {
                                "${it.label}\n" + NumberFormat.getPercentInstance().format(it.score).trim()
                            }
                            resultAnalyzer = tampilResult
                            moveToResult()
                        }
                    }

                }
            )
            currentImageUri?.let { imageClassifierHelper.classifyStaticImage(it) }
        } else {
            showToast(getString(R.string.empty_image))
        }
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(EXTRA_RESULT, resultAnalyzer)
        intent.putExtra(EXTRA_IMAGE, currentImageUri)
        startActivity(intent)
    }

    private val openGallerry = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            cropImage(uri)
            showImage()
        } else {
            showToast(getString(R.string.empty_image))
    }}

    private fun cropImage(uri: Uri) {
        val destinasiFile = "${UUID.randomUUID()}.jpg"
        val destinasiUri = Uri.fromFile(File(filesDir, destinasiFile))

        val option = UCrop.Options()
        option.setCompressionQuality(100)

        UCrop.of(uri, destinasiUri).withOptions(option).withAspectRatio(0f, 0f).useSourceImageAspectRatio().withMaxResultSize(3000, 3000).start(this)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            currentImageUri = UCrop.getOutput(data!!)
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast(cropError.toString())
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_RESULT = "extra_result"
    }
}