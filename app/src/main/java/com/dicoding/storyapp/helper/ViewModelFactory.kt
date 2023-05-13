package com.dicoding.storyapp.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.ui.home.HomeViewModel
import com.dicoding.storyapp.ui.main.MainViewModel

class ViewModelFactory(
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository
    ) : NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context) : ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.providePreferences(context),
                    Injection.provideRepository()
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userPreferences, userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userPreferences, userRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}