package com.dicoding.asclepius.view.ui.History

import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.repository.HistoryRepository

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    fun getHistory()= historyRepository.showHistory()
    fun deleteHistory(historyEntity: HistoryEntity) {
        historyRepository.deleteHistory(historyEntity)
    }
}