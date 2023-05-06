package com.dicoding.storyapp.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.lib.User.LoginResponse
import com.dicoding.storyapp.data.remote.API.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private var _user = MutableLiveData<LoginResponse>()
    val user: LiveData<LoginResponse> = _user
    private var _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(context: Context, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context).postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _user.postValue(response.body())
                    _isLogin.postValue(true)
                } else {
                    _isLogin.postValue(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                t.message?.let { Log.d("Failure", it) }
            }

        })
    }
}