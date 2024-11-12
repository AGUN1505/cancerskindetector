package com.dicoding.asclepius.data.di

import android.content.Context
import com.dicoding.asclepius.data.local.room.HistoryDatabase
import com.dicoding.asclepius.data.repository.HistoryRepository
import com.dicoding.asclepius.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): HistoryRepository {
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        val appExecutors = AppExecutors()
        return HistoryRepository.getInstance(dao, appExecutors)
    }
}