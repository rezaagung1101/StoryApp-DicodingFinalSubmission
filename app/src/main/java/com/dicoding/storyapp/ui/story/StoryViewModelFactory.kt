package com.dicoding.storyapp.ui.story

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.database.StoryDatabase
import com.dicoding.storyapp.data.remote.API.ApiService
import com.dicoding.storyapp.data.remote.repository.StoryRepository

class StoryViewModelFactory(val context: Context, private val apiService: ApiService) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryPagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val database = StoryDatabase.getDatabase(context)
            return StoryPagerViewModel(StoryRepository(
                    database,
                    apiService
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}