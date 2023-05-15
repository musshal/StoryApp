package com.dicoding.storyapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.data.source.local.entity.UserEntity
import com.dicoding.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
    ) : ViewModel() {

    fun getLogin() : LiveData<UserEntity> = userPreferences.getLogin().asLiveData()

    fun getDetailStory(token: String, id: String) = storyRepository.getDetailStory(token, id)

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }
}