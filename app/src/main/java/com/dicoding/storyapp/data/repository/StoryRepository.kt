package com.dicoding.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.MessageResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody

class StoryRepository private constructor(private val apiService: ApiService) {

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }

    fun addNewStory(
        token: String,
        description: String,
        photo: MultipartBody.Part
    ) : LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            if (token.isNotBlank()) {
                val responseBody = apiService.addNewStory("Bearer $token", description, photo)
                emit(Result.Success(responseBody))
            } else {
                val responseBody = apiService.addNewStory(description, photo)
                emit(Result.Success(responseBody))
            }
        } catch (e: Exception) {
            Log.d("StoryRepository", "addNewStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAllStories(token: String) : LiveData<Result<AllStoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val responseBody = apiService.getAllStories("Bearer $token")
            emit(Result.Success(responseBody))
        } catch (e: Exception) {
            Log.d("StoryRepository", "getAllStories: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }
}