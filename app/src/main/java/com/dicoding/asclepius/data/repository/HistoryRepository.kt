package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.local.room.HistoryDao
import com.dicoding.asclepius.utils.AppExecutors

class HistoryRepository(
    private val historyDao: HistoryDao,
    private val appExecutors: AppExecutors
) {

    fun saveHistory(historyEntity: HistoryEntity) {
        appExecutors.diskIO.execute {
            historyDao.saveResult(historyEntity)
        }
    }

    fun showHistory(): LiveData<List<HistoryEntity>> {
        return historyDao.showResult()
    }

    fun deleteHistory(historyEntity: HistoryEntity) {
        appExecutors.diskIO.execute {
            historyDao.deleteResult(historyEntity)
        }
    }

    fun getHistoryByImageUri(imageUri: String, callback: (HistoryEntity?) -> Unit) {
        appExecutors.diskIO.execute{
            val result = historyDao.getResultByImageUri(imageUri)
            appExecutors.mainThread.execute {
                callback(result)
            }
        }
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(
            historyDao: HistoryDao,
            appExecutors: AppExecutors
        ): HistoryRepository =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(historyDao, appExecutors)
            }.also { instance = it }
    }
}