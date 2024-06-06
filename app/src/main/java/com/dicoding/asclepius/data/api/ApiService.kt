package com.dicoding.asclepius.data.api

import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.data.response.NewsResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @GET("top-headlines")
    fun getHealthNews(
        @Query("q") query: String,
        @Query("category") category: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsResponse>

}