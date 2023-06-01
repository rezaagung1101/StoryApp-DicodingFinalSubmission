package com.dicoding.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.utils.Helper

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var name: String
    private lateinit var createdAt: String
    private lateinit var description: String
    private lateinit var photoUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        name = intent.getStringExtra("NAME").toString()
        createdAt = intent.getStringExtra("CREATEDAT").toString()
        description = intent.getStringExtra("DESCRIPTION").toString()
        photoUrl = intent.getStringExtra("PHOTOURL").toString()
        setupAction(name, createdAt, description, photoUrl)
    }

    private fun setupAction(
        name: String,
        createdAt: String,
        description: String,
        photoUrl: String,
    ) {
        binding.apply {
            tvName.text = name
            tvCreatedDate.text = createdAt
            tvDescriptionValue.text = description
            Glide.with(applicationContext)
                .load(photoUrl)
                .into(ivStory)
            try {
                val lat = intent.getStringExtra("LATITUDE")
                val lon = intent.getStringExtra("LONGITUDE")
                if (lat != null && lon != null) {
                    tvLocation.text = Helper.parseAddress(this@DetailActivity, lat.toDouble(), lon.toDouble())
                }else{
                    tvLocation.isVisible = false
                    ivLocation.isVisible = false
                }
            } catch (e: Exception) {
                tvLocation.isVisible = false
                ivLocation.isVisible = false
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}