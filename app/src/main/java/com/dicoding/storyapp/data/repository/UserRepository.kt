package com.dicoding.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.source.remote.request.LoginRequest
import com.dicoding.storyapp.data.source.remote.request.RegisterRequest
import com.dicoding.storyapp.data.source.remote.response.LoginResponse
import com.dicoding.storyapp.data.source.remote.response.MessageResponse
import com.dicoding.storyapp.data.source.remote.retrofit.ApiService

class UserRepository private constructor(private val apiService: ApiService) {

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService) : UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }

    fun register(registerRequest: RegisterRequest) : LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val responseBody = apiService.register(registerRequest)
            emit(Result.Success(responseBody))
        } catch (e: Exception) {
            Log.d("UserRepository", "register: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(loginRequest: LoginRequest) : LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val responseBody = apiService.login(loginRequest)
            emit(Result.Success(responseBody))
        } catch (e: Exception) {
            Log.d("UserRepository", "login: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }
}