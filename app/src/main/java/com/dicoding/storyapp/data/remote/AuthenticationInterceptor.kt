package com.dicoding.storyapp.data.remote

import android.content.Context
import com.dicoding.storyapp.utils.preferences.UserPreference
import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor(context: Context) : Interceptor {
    private val userPreference = UserPreference(context)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        userPreference.getToken()?.let { token->
            request
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "multiple/form-data")
        }
        return chain.proceed(request.build())
    }
}