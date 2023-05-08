package com.dicoding.storyapp.data.remote.API

import com.dicoding.storyapp.data.lib.User.LoginResponse
import com.dicoding.storyapp.data.lib.User.SignUpResponse
import com.dicoding.storyapp.data.lib.story.StoryResponse
import com.dicoding.storyapp.data.lib.story.StoryUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun postSignUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignUpResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories?location=1")
    fun getAllStoryLocation(
        @Query("size") size: Int
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getStoryList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse


    @Multipart
    @POST("stories")
    fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call <StoryUpload>

    @Multipart
    @POST("stories")
    fun postStoryWithLocation(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call <StoryUpload>
}