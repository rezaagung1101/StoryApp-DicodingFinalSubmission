package com.dicoding.storyapp.ui.signup

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.lib.User.LoginResponse
import com.dicoding.storyapp.data.lib.User.SignUpResponse
import com.dicoding.storyapp.data.remote.API.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {
    private var _user = MutableLiveData<LoginResponse>()
    val user: LiveData<LoginResponse> = _user
    private var _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun signUp(context: Context, name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(context).postSignUp(name, email, password)
        client.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _isLogin.postValue(true)
                } else {
                    _isLogin.postValue(false)
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                _isLoading.value = false
                t.message?.let { Log.d("Failure", it) }
            }
        })
    }
}