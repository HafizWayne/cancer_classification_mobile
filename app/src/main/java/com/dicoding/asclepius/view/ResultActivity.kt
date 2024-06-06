package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    private lateinit var resultViewModel: ResultViewModel
    private lateinit var adapter: NewsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        resultViewModel = ViewModelProvider(this)[ResultViewModel::class.java]

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        val result = intent.getStringExtra(EXTRA_RESULT)

        binding.resultImage.setImageURI(Uri.parse(imageUri))
        binding.resultText.text = result

        val layoutManager = LinearLayoutManager(this)
        binding.rvAdapter.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvAdapter.addItemDecoration(itemDecoration)

        // Observe LiveData
        resultViewModel.newscancer.observe(this) { user ->
            setListData(user)
        }

        resultViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun setListData(news: List <ArticlesItem>) {
        adapter = NewsAdapter(news)
        binding.rvAdapter.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"


    }



}