package com.dicoding.storyapp.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dicoding.storyapp.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        private val USER_ID = stringPreferencesKey("userId")
        private val NAME = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences =
            INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
        }
    }

    suspend fun setLogin(user: UserEntity) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.userId
            preferences[NAME] = user.name
            preferences[TOKEN] = user.token
        }
    }

    fun getLogin(): Flow<UserEntity> {
        return dataStore.data.map { preferences ->
            UserEntity(
                preferences[USER_ID] ?: "",
                preferences[NAME] ?: "",
                preferences[TOKEN] ?: ""
            )
        }
    }

    suspend fun deleteLogin() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}