package com.dicoding.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import kotlinx.coroutines.launch

class HomeViewModel(private val preferences: UserPreferences) : ViewModel() {
    fun deleteLogin() {
        viewModelScope.launch {
            preferences.deleteLogin()
        }
    }
}