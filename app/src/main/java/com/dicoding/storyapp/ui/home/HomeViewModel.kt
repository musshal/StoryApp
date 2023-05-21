package com.dicoding.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.data.source.local.entity.StoryEntity
import com.dicoding.storyapp.data.source.local.entity.UserEntity
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.source.remote.response.StoryResponse
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
    ) : ViewModel() {

    fun getLogin() : LiveData<UserEntity> = userPreferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }

    fun getAllStories(token: String): LiveData<PagingData<StoryEntity>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)

    fun getBookmarkedStories() = storyRepository.getBookmarkedStories()

    fun saveStory(storyEntity: StoryEntity) {
        viewModelScope.launch {
            storyRepository.setStoryBookmark(storyEntity, true)
        }
    }

    fun deleteStory(story: StoryEntity) {
        viewModelScope.launch {
            storyRepository.setStoryBookmark(story, false)
        }
    }
}