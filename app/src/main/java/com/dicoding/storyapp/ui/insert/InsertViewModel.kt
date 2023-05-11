package com.dicoding.storyapp.ui.insert

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.remote.response.MessageResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InsertViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    companion object {
        private val TAG = "InsertViewModel"
    }

    fun addNewStory(description: String, photo: MultipartBody.Part) {
        _isLoading.value = true
        _isSuccess.value = false
        _isError.value = false

        val client = ApiConfig.getApiService().addNewStory(description, photo)

        client.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        _isLoading.value = false
                        _isSuccess.value = true
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
}
