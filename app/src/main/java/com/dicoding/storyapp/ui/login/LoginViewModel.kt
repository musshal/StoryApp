package com.dicoding.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.entity.UserEntity
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

class LoginViewModel(private val preferences: Preferences) : ViewModel() {
}