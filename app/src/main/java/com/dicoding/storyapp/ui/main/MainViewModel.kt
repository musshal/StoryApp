package com.dicoding.storyapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.preferences.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: UserPreference) : ViewModel() {
    fun getLogin() {
        viewModelScope.launch {
            preferences.getLogin()
        }
    }
}