package com.dicoding.asclepius.view

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.repository.HistoryRepository

class ResultViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _snackBar = MutableLiveData<String>()
    val snackBar: LiveData<String> = _snackBar

    fun saveHistory(imageUri: Uri, prediction: String) {
        val imageToString = imageUri.toString()

        historyRepository.getHistoryByImageUri(imageToString) { result ->
            if (result == null) {
                val historyEntity = HistoryEntity(
                    imageUri = imageUri,
                    prediction = prediction

                )
                historyRepository.saveHistory(historyEntity)
                _snackBar.value = "History Saved"
            } else {
                _snackBar.value = "Sudah pernah dianalisa"
            }
        }
    }
}