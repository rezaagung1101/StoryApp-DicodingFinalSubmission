package com.dicoding.storyapp.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.storyapp.data.database.StoryDatabase
import com.dicoding.storyapp.data.lib.story.Story
import com.dicoding.storyapp.data.remote.API.ApiService
import com.dicoding.storyapp.data.remote.StoryRemoteMediator

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val token:String
) {
    fun getPagingStory(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService,token),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStory() }
        ).liveData
    }
}