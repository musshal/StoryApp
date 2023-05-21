package com.dicoding.storyapp.data.source.remote.retrofit

import com.dicoding.storyapp.data.source.remote.request.LoginRequest
import com.dicoding.storyapp.data.source.remote.request.RegisterRequest
import com.dicoding.storyapp.data.source.remote.response.AllStoriesResponse
import com.dicoding.storyapp.data.source.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.source.remote.response.LoginResponse
import com.dicoding.storyapp.data.source.remote.response.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    suspend fun register(@Body request: RegisterRequest): MessageResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @Multipart
    @POST("stories/guest")
    suspend fun addNewStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): MessageResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): MessageResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): AllStoriesResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
    ): AllStoriesResponse

    @GET("stories?location=1")
    suspend fun getAllStoriesWithLocation(
        @Header("Authorization") token: String
    ): AllStoriesResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoryResponse
}