package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.api.ApiConfig
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.data.response.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultViewModel: ViewModel() {
    private val _newscancer = MutableLiveData<List<ArticlesItem>>()
    val newscancer: LiveData<List<ArticlesItem>> = _newscancer

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getData() // Fetch initial data on ViewModel creation
    }

    private fun getData() {
        _isLoading.value = true
        val service = ApiConfig.getApiService().getHealthNews("cancer", "health", "en", "710cc0d588c54176b038777ebcc92dd1")
        service.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    responseBody?.let {
                        _newscancer.value = it.articles!!
                    }
                } else {
                    Log.e(MainActivity.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}