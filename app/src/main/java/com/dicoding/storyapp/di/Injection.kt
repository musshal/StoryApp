package com.dicoding.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.UserRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

object Injection {
    fun providePreferences(context: Context) : UserPreferences {
        val dataStore = context.dataStore

        return UserPreferences.getInstance(dataStore)
    }

    fun provideRepository() : UserRepository {
        val apiService = ApiConfig.getApiService()

        return UserRepository.getInstance(apiService)
    }
}