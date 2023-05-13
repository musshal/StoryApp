package com.dicoding.storyapp.data.remote.request

import okhttp3.MultipartBody

data class NewStoryRequest(
    val token: String,
    val description: String,
    val photo: MultipartBody.Part
)