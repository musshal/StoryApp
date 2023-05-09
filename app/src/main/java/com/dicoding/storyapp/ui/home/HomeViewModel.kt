package com.dicoding.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.preferences.UserPreference
import kotlinx.coroutines.launch

class HomeViewModel(private val preference: UserPreference) : ViewModel() {
    fun deleteLogin() {
        viewModelScope.launch {
            preference.deleteLogin()
        }
    }
}