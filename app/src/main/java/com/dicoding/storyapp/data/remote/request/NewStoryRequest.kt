package com.dicoding.storyapp.data.remote.request

import java.io.File

data class NewStoryRequest(
    val description: String,
    val photo: File,
    val lat: Float,
    val lon: Float
)
