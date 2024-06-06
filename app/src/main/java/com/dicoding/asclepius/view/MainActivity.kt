package com.dicoding.asclepius.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.api.ApiConfig
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.model.ImageData
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import org.tensorflow.lite.task.vision.classifier.Classifications
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var currentImageUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
    }



    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) {
            showToast(getString(R.string.no_media_selected))
        } else {
            // Menggunakan UCrop untuk memotong dan memutar gambar sebelumnya yang dipilih
            UCrop.of(uri, Uri.fromFile(File(cacheDir, "cropped_image")))
                .withOptions(UCrop.Options().apply {
                    setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
                    setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                    setStatusBarColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                })
                .start(this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                currentImageUri = it // Update currentImageUri dengan gambar yang baru
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.let {
                showToast(getString(R.string.crop_error_message))
            }
        }
    }



    private fun showImage() {
        // Menampilkan gambar sesuai dengan URI yang dipilih.
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            val contentResolver = applicationContext.contentResolver
            val inputStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding.previewImageView.setImageBitmap(bitmap)
        }
    }


    private fun analyzeImage(imageUri: Uri) {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                // Menemukan kategori dengan persentase tertinggi
                                val highestCategory = it[0].categories.maxByOrNull { it?.score ?: 0f }
                                highestCategory?.let { category ->
                                    // Membuat string untuk kategori tertinggi
                                    val displayResult = "${category.label} " +
                                            NumberFormat.getPercentInstance().format(category.score).trim()
                                    moveToResult(displayResult)
                                }
                            }
                        }
                    }
                }
            }
        )
        // Memulai klasifikasi gambar
        imageClassifierHelper.classifyStaticImage(imageUri)
    }


    private fun moveToResult(displayResult: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        intent.putExtra(ResultActivity.EXTRA_RESULT, displayResult)
        startActivity(intent)
    }

    companion object {
        const val TAG = "MainViewModel"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
            }




