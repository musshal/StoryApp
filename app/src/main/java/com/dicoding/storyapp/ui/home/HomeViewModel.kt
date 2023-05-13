package com.dicoding.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val preferences: UserPreferences,
    private val userRepository: UserRepository
    ) : ViewModel() {

    fun deleteLogin() {
        viewModelScope.launch {
            preferences.deleteLogin()
        }
    }
}