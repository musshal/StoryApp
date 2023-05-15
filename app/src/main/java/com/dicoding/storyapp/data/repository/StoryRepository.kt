package com.dicoding.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.storyapp.data.local.entity.StoryEntity
import com.dicoding.storyapp.data.local.room.StoryDao
import com.dicoding.storyapp.data.remote.request.NewStoryRequest
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.remote.response.MessageResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.helper.AppExecutors

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDao: StoryDao,
    ) {

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            storyDao: StoryDao,
            appExecutors: AppExecutors
        ) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDao)
            }.also { instance = it }
    }

    fun addNewStory(newStoryRequest: NewStoryRequest) : LiveData<Result<MessageResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                if (newStoryRequest.token.isNotBlank()) {
                    val responseBody = apiService.addNewStory(
                        "Bearer ${newStoryRequest.token}",
                        newStoryRequest.description,
                        newStoryRequest.photo
                    )
                    emit(Result.Success(responseBody))
                } else {
                    val responseBody = apiService.addNewStory(
                        newStoryRequest.description,
                        newStoryRequest.photo
                    )
                    emit(Result.Success(responseBody))
                }
            } catch (e: Exception) {
                Log.d("StoryRepository", "addNewStory: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getAllStories(token: String) : LiveData<Result<List<StoryEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val responseBody = apiService.getAllStories("Bearer $token")
            val stories = responseBody.listStory
            val storyList = stories.map { story ->
                val isBookmarked = storyDao.isStoryBookmarked(story.id)
                StoryEntity(
                    story.id,
                    story.name,
                    story.description,
                    story.photoUrl,
                    story.createdAt,
                    isBookmarked
                )
            }
            storyDao.deleteAll()
            storyDao.insertStory(storyList)
        } catch (e: Exception) {
            Log.d("StoryRepository", "getAllStories: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<StoryEntity>>> = storyDao.getAllStories().map {
            Result.Success(it)
        }
        emitSource(localData)
    }

    fun getDetailStory(token: String, id: String) : LiveData<Result<DetailStoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val responseBody = apiService.getDetailStory("Bearer $token", id)
                emit(Result.Success(responseBody))
            } catch (e: Exception) {
                Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }
    fun setStoryBookmark(story: StoryEntity, bookmarkState: Boolean) {
        story.isBookmarked = bookmarkState
        storyDao.updateStory(story)
    }

    fun getBookmarkedStories() : LiveData<List<StoryEntity>> = storyDao.getBookmarkedStories()
}