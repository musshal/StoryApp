package com.dicoding.storyapp.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.ui.home.HomeViewModel
import com.dicoding.storyapp.ui.main.MainViewModel

class ViewModelFactory(
    private val preferences: UserPreferences,
    private val userRepository: UserRepository
    ) : NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preferences, userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(preferences, userRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}