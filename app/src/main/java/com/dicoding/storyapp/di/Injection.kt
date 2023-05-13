package com.dicoding.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

object Injection {

    private val apiService = ApiConfig.getApiService()

    fun providePreferences(context: Context) : UserPreferences {
        val dataStore = context.dataStore

        return UserPreferences.getInstance(dataStore)
    }

    fun provideUserRepository() : UserRepository {
        return UserRepository.getInstance(apiService)
    }

    fun provideStoryRepository() : StoryRepository {
        return StoryRepository.getInstance(apiService)
    }
}