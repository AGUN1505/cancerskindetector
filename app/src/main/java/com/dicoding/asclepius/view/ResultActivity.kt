package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.NavActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.google.android.material.snackbar.Snackbar

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: ResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result : ResultViewModelFactory = ResultViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, result)[ResultViewModel::class.java]
        
        viewModel.snackBar.observe(this) {
            Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_SHORT).show()
        }
        
        val imageUri: Uri? = intent.getParcelableExtra(EXTRA_IMAGE)
        val prediction = intent.getStringExtra(EXTRA_RESULT)

        binding.resultImage.setImageURI(imageUri)
        binding.resultText.text = prediction

        binding.btnBck.setOnClickListener {
            val intent = Intent(this, NavActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }

        binding.btnSv.setOnClickListener {
            if (imageUri != null && prediction != null) {
                viewModel.saveHistory(imageUri, prediction)
            } else {
                Snackbar.make(window.decorView.rootView, "History Not Saved", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_RESULT = "extra_result"
    }
}