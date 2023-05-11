package com.dicoding.storyapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.preferences.UserPreference
import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val preference: UserPreference) : ViewModel() {

    companion object {
        private val TAG = "HomeViewModel"
    }

    private fun getAllStories() {
        viewModelScope.launch {
            preference.getLogin().collect { user ->
                val token = "Bearer ${user.token}"
                val client = ApiConfig.getApiService().getAllStories(
                    token, 1, 20
                )
                client.enqueue(object : Callback<AllStoriesResponse> {
                    override fun onResponse(
                        call: Call<AllStoriesResponse>,
                        response: Response<AllStoriesResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                        } else {
                            Log.e(TAG, "OnFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                        Log.e(TAG, "OnFailure: ${t.message}")
                    }
                })
            }
        }
    }

    fun deleteLogin() {
        viewModelScope.launch {
            preference.deleteLogin()
        }
    }
}