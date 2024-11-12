package com.dicoding.asclepius.view.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.remote.response.NewsResponse
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import retrofit2.*

class HomeViewModel : ViewModel() {

    private val _article = MutableLiveData<List<ArticlesItem>>()
    val news: LiveData<List<ArticlesItem>> = _article

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getNews() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getArticle(BuildConfig.API_KEY)
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _article.value = response.body()?.articles?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Kesalahan: ${response.message()}"
                }
            }

            override fun onFailure(p0: Call<NewsResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Kesalahan: ${t.message}"
            }
        })
    }
}