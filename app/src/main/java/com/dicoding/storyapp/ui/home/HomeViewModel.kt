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

    val story: LiveData<PagingData<StoryResponse>> = storyRepository.getStories().cachedIn(viewModelScope)

    fun getLogin() : LiveData<UserEntity> = userPreferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }

    fun getAllStories(token: String) = storyRepository.getAllStories(token)

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