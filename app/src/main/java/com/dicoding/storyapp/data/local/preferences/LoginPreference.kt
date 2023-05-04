package com.dicoding.storyapp.data.local.preferences

import android.content.Context
import com.dicoding.storyapp.data.local.entity.LoginEntity

internal class LoginPreference(context: Context) {

    companion object {
        private const val PREFS_NAME = "login_pref"
        private const val USER_ID = "user_id"
        private const val NAME = "name"
        private const val TOKEN = "token"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setLogin(value: LoginEntity) {
        val editor = preferences.edit()
        editor.putString(USER_ID, value.userId)
        editor.putString(NAME, value.name)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getLogin(): LoginEntity {
        val entity = LoginEntity()
        entity.userId = preferences.getString(USER_ID, "")
        entity.name = preferences.getString(NAME, "")
        entity.token = preferences.getString(TOKEN, "")

        return  entity
    }
}