package com.dicoding.storyapp.ui.insert

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.source.local.entity.UserEntity
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.data.source.remote.request.NewStoryRequest
import com.dicoding.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class   InsertViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getLogin() : LiveData<UserEntity> = userPreferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }

    fun addNewStory(newStoryRequest: NewStoryRequest) = storyRepository.addNewStory(newStoryRequest)
}