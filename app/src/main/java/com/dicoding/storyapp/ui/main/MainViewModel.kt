package com.dicoding.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.entity.UserEntity
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.request.RegisterRequest
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val preferences: UserPreferences,
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
    ) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean?>()
    val isError: LiveData<Boolean?> = _isError

    private val _stories = MutableLiveData<ArrayList<StoryResponse>>()
    val stories: LiveData<ArrayList<StoryResponse>> = _stories

    companion object {
        private val TAG = "MainViewModel"
    }

    fun register(registerRequest: RegisterRequest) = userRepository.register(registerRequest)

    fun login(loginRequest: LoginRequest) = userRepository.login(loginRequest)

    fun setLogin(user: UserEntity) { viewModelScope.launch { preferences.setLogin(user) } }

    fun getLogin() : LiveData<UserEntity> = preferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { preferences.deleteLogin() } }

    private fun getAllStories(token: String) = storyRepository.getAllStories(token)
}