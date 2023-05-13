package com.dicoding.storyapp.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.ui.insert.InsertViewModel
import com.dicoding.storyapp.ui.main.MainViewModel

class ViewModelFactory(
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
    ) : NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context) : ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.providePreferences(context),
                    Injection.provideUserRepository(),
                    Injection.provideStoryRepository()
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userPreferences, userRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(InsertViewModel::class.java) -> {
                InsertViewModel(userPreferences, storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}