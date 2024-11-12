package com.dicoding.asclepius.data.remote.retrofit

import com.dicoding.asclepius.data.remote.response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("top-headlines?q=cancer&category=health&language=en")
    fun getArticle(
        @Query("apiKey")
        apiKey: String
    ): Call<NewsResponse>
}