package com.dicoding.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.entity.UserEntity
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class   LoginViewModel(private val preferences: UserPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    companion object {
        private val TAG = "LoginViewModel"
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _isSuccess.value = false
        _isError.value = false

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
                        _isSuccess.value = true

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
}