package com.dicoding.storyapp.ui.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.database.StoryDao
import com.dicoding.storyapp.data.database.StoryDatabase
import com.dicoding.storyapp.data.lib.story.Story
import com.dicoding.storyapp.data.lib.story.StoryResponse
import com.dicoding.storyapp.data.remote.API.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainViewModel(application: Application) : ViewModel() {
    private var _listStory = MutableLiveData<ArrayList<Story>>()
    val listStory: LiveData<ArrayList<Story>> = _listStory
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val mStoryDao: StoryDao
    init {
        val db = StoryDatabase.getDatabase(application)
        mStoryDao = db.storyDao()
    }

    fun getStoryList(context: Context) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context).getAllStories()
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStory.postValue(response.body()?.listStory)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                t.message?.let { Log.d("Data not found!", it) }
            }
        })
    }
}