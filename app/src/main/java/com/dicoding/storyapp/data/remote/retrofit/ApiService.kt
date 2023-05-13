package com.dicoding.storyapp.data.remote.retrofit

import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.request.RegisterRequest
import com.dicoding.storyapp.data.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.response.MessageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): MessageResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @Multipart
    @POST("stories/guest")
    suspend fun addNewStory(
        @Part("description") description: String,
        @Part photo: MultipartBody.Part
    ): MessageResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: String,
        @Part photo: MultipartBody.Part
    ): MessageResponse

    @GET("stories")
    suspend fun getAllStories(@Header("Authorization") token: String): AllStoriesResponse

    @GET("stories?location=1")
    fun getAllStoriesByLocation(
        @Header("Authorization") token: String,
    ): Call<AllStoriesResponse>

    @GET("stories/{id}")
    fun getDetailStory(@Path("id") id: String): Call<DetailStoryResponse>
}