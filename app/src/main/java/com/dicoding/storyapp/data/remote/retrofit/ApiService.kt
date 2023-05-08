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
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<MessageResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("stories")
    fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: String,
        @Part photo: MultipartBody.Part
    ): Call<MessageResponse>

    @Multipart
    @POST("stories/guest")
    fun addNewStory(
        @Part("description") description: String,
        @Part photo: MultipartBody.Part
    ): Call<MessageResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Boolean
    ): Call<AllStoriesResponse>

    @GET("stories/{id}")
    fun getDetailStory(@Path("id") id: String): Call<DetailStoryResponse>
}