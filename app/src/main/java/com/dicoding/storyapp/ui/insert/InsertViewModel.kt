package com.dicoding.storyapp.ui.insert

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.entity.UserEntity
import com.dicoding.storyapp.data.local.datastore.UserPreferences
import com.dicoding.storyapp.data.remote.request.NewStoryRequest
import com.dicoding.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class InsertViewModel(
    private val preferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getLogin() : LiveData<UserEntity> = preferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { preferences.deleteLogin() } }

    fun addNewStory(newStoryRequest: NewStoryRequest) = storyRepository.addNewStory(newStoryRequest)
}