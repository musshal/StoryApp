package com.dicoding.storyapp.data.remote.request

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class NewStoryRequest(
    val token: String,
    val description: RequestBody,
    val photo: MultipartBody.Part
)