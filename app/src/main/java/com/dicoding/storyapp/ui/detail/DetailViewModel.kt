package com.dicoding.storyapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.datastore.UserPreferences
import kotlinx.coroutines.launch

class DetailViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }
}