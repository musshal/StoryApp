package com.dicoding.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.data.source.local.entity.UserEntity
import kotlinx.coroutines.launch

class MapsViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
    ): ViewModel() {

    fun getLogin(): LiveData<UserEntity> = userPreferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }

    fun getAllStoriesWithLocation(token: String) =
        storyRepository.getAllStoriesWithLocation(token)
}