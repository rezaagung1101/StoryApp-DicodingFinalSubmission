package com.dicoding.storyapp.ui.maps

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.lib.story.Story
import com.dicoding.storyapp.data.lib.story.StoryResponse
import com.dicoding.storyapp.data.remote.API.ApiConfig
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {
    private var _storyList = MutableLiveData<List<Story>>()
    val storyList: LiveData<List<Story>> = _storyList
    private var _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private var _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private var _isLocationPicked = MutableLiveData<Boolean>() // init for location new story not selected
    val isLocationPicked: LiveData<Boolean> = _isLocationPicked
    private var _coordinateLatitude = MutableLiveData<Double>()
    val coordinateLatitude:LiveData<Double> = _coordinateLatitude
    private var _coordinateLongitude = MutableLiveData<Double>()
    val coordinateLongitude: LiveData<Double> = _coordinateLongitude
    val coordinateLocation = MutableLiveData(LatLng(-2.548926, 118.0148634))
    fun loadStoryLocationData(context: Context) {
        val client = ApiConfig.getApiService(context).getAllStoryLocation(100)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                    _isLoading.value = false
                if (response.isSuccessful) {
                    _isError.postValue(false)
                    _storyList.postValue(response.body()?.listStory)
                } else {
                    _isError.postValue(true)
                    _error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(true)
                Log.e("Failed to Fetch Data", "onFailure Call: ${t.message}")
                _error.postValue("${context.getString(R.string.maps_error)} : ${t.message}")
            }
        })
    }
}