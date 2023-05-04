package com.dicoding.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.lib.story.Story
import com.dicoding.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    private lateinit var storyData : Story
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storyData = intent.getParcelableExtra<Story>("STORY") as Story
        setupAction(storyData)
    }
    private fun setupAction(story : Story){
        binding.apply{
            tvName.text = story.name
            tvCreatedDate.text = story.createdAt
            tvDescriptionValue.text = story.description
            Glide.with(applicationContext)
                .load(story.photoUrl)
                .into(ivStory)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}