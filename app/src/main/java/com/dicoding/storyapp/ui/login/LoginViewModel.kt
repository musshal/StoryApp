package com.dicoding.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.entity.UserEntity
import com.dicoding.storyapp.data.local.preferences.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val preferences: UserPreference) : ViewModel() {
    fun setLogin(user: UserEntity) {
        viewModelScope.launch {
            preferences.setLogin(user)
        }
    }
}