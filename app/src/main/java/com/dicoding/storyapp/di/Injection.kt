package com.dicoding.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.storyapp.data.source.local.datastore.SettingPreferences
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.data.source.local.room.StoryDatabase
import com.dicoding.storyapp.data.source.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.helper.AppExecutors

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

    fun provideUserRepository() : UserRepository = UserRepository.getInstance(apiService)

    fun provideStoryRepository(context: Context) : StoryRepository{
        val database = StoryDatabase.getInstance(context)
        val storyDao = database.storyDao()
        val appExecutors = AppExecutors()

        return StoryRepository.getInstance(apiService, storyDao, appExecutors)
    }
}