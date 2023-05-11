package com.dicoding.storyapp.ui.home

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
                        TODO("Not yet implemented")
                    }

                    override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                        TODO("Not yet implemented")
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