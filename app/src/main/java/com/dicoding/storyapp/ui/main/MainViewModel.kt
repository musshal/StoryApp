package com.dicoding.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.entity.UserEntity
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.request.RegisterRequest
import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.response.LoginResultResponse
import com.dicoding.storyapp.data.remote.response.MessageResponse
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val preferences: UserPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean?>()
    val isError: LiveData<Boolean?> = _isError

    private val _user = MutableLiveData<LoginResultResponse>()
    val user: LiveData<LoginResultResponse> = _user

    private val _stories = MutableLiveData<ArrayList<StoryResponse>>()
    val stories: LiveData<ArrayList<StoryResponse>> = _stories

    companion object {
        private val TAG = "MainViewModel"
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        _isError.value = null

        val client = ApiConfig.getApiService().register(
            RegisterRequest(name, email, password)
        )
        client.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        _isLoading.value = false
                        _isError.value = false
                    }
                } else {
                    Log.e(TAG, "OnFailure: ${response.message()}")

                    _isLoading.value = false
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e(TAG, "OnFailure: ${t.message}")

                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _isError.value = null

        val client = ApiConfig.getApiService().login(LoginRequest(email, password))

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        _isLoading.value = false
                        _isError.value = false
                        _user.value = responseBody.loginResult
                        setLogin(UserEntity(
                            responseBody.loginResult.userId,
                            responseBody.loginResult.name,
                            responseBody.loginResult.token
                        ))
                    }
                } else {
                    Log.e(TAG, "OnFailure: ${response.message()}")

                    _isLoading.value = false
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "OnFailure: ${t.message}")

                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun setLogin(user: UserEntity) {
        viewModelScope.launch {
            preferences.setLogin(user)
        }
    }

    fun getLogin() : LiveData<UserEntity> = preferences.getLogin().asLiveData()

    private fun getAllStories(token: String) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getAllStories(token, 1, 20)

        client.enqueue(object : Callback<AllStoriesResponse> {
            override fun onResponse(
                call: Call<AllStoriesResponse>,
                response: Response<AllStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        _isLoading.value = false
                        _isError.value = false
                        _stories.value = responseBody.listStory
                    }
                } else {
                    Log.e(TAG, "OnFailure: ${response.message()}")

                    _isLoading.value = false
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                Log.e(TAG, "OnFailure: ${t.message}")

            }
        })
    }

    fun deleteLogin() {
        viewModelScope.launch {
            preferences.deleteLogin()
        }
    }
}