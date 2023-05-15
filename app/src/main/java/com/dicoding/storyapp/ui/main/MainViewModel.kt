package com.dicoding.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.datastore.SettingPreferences
import com.dicoding.storyapp.data.local.entity.UserEntity
import com.dicoding.storyapp.data.local.datastore.UserPreferences
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.request.RegisterRequest
import com.dicoding.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferences: UserPreferences,
    private val settingPreferences: SettingPreferences,
    private val userRepository: UserRepository
    ) : ViewModel() {

    fun register(registerRequest: RegisterRequest) = userRepository.register(registerRequest)

    fun login(loginRequest: LoginRequest) = userRepository.login(loginRequest)

    fun setLogin(user: UserEntity) { viewModelScope.launch { userPreferences.setLogin(user) } }

    fun getLogin() : LiveData<UserEntity> = userPreferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }

    fun getThemeSetting() : LiveData<Boolean> = settingPreferences.getThemeSetting().asLiveData()
}