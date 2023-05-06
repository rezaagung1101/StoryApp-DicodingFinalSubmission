package com.dicoding.storyapp.ui.story

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.lib.story.StoryUpload
import com.dicoding.storyapp.data.remote.API.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel : ViewModel() {

    private var _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun postStory(context: Context, imageMultipart: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context).postStory(imageMultipart, description)
        client.enqueue(object : Callback<StoryUpload>{
            override fun onResponse(call: Call<StoryUpload>, response: Response<StoryUpload>) {
                _isLoading.value = false
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody!= null && !responseBody.error){
                        _isLogin.postValue(true)
                    }
                }else{
                    _isLogin.postValue(false)
                }
            }

            override fun onFailure(call: Call<StoryUpload>, t: Throwable) {
                t.message?.let{ Log.d("Failed to Upload",it)}
            }
        })

    }
}