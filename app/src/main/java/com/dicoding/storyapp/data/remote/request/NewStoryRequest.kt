package com.dicoding.storyapp.data.remote.request

import okhttp3.MultipartBody

data class NewStoryRequest(
    val description: String,
    val photo: MultipartBody.Part,
    val lat: Float? = null,
    val lon: Float? = null,
)
