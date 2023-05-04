package com.dicoding.storyapp.data.lib.story

import com.google.gson.annotations.SerializedName

data class StoryUpload(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)