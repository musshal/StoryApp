package com.dicoding.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.preferences.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: UserPreference) : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        getLogin()
    }

    private fun getLogin() {
        viewModelScope.launch {
            preferences.getLogin().collect { user ->
                _isLoggedIn.value =
                    user.userId.isNotEmpty() && user.name.isNotEmpty() && user.token.isNotEmpty()
            }
        }
    }
}