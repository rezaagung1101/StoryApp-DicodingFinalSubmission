package com.dicoding.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.lib.story.Story
import com.dicoding.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    private lateinit var name : String
    private lateinit var createdAt : String
    private lateinit var description : String
    private lateinit var photoUrl : String
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
    private fun setupAction(name : String, createdAt : String, description : String, photoUrl : String){
        binding.apply{
            tvName.text = name
            tvCreatedDate.text = createdAt
            tvDescriptionValue.text = description
            Glide.with(applicationContext)
                .load(photoUrl)
                .into(ivStory)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}