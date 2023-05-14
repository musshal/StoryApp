package com.dicoding.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.storyapp.data.local.datastore.SettingPreferences
import com.dicoding.storyapp.data.local.datastore.UserPreferences
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository

object Injection {

    private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "user_preferences"
    )
    private val Context.settingDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "settings"
    )
    private val apiService = ApiConfig.getApiService()

    fun provideUserPreferences(context: Context) : UserPreferences {
        val dataStore = context.userDataStore

        return UserPreferences.getInstance(dataStore)
    }

    fun provideSettingPreferences(context: Context) : SettingPreferences {
        val dataStore = context.settingDataStore

        return SettingPreferences.getInstance(dataStore)
    }

    fun provideUserRepository() : UserRepository {
        return UserRepository.getInstance(apiService)
    }

    fun provideStoryRepository() : StoryRepository {
        return StoryRepository.getInstance(apiService)
    }
}