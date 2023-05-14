package com.dicoding.storyapp.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.datastore.UserPreferences
import kotlinx.coroutines.launch

class BookmarkViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    fun deleteLogin() { viewModelScope.launch { userPreferences.deleteLogin() } }
}