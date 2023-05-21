package com.dicoding.storyapp.data.source.remote.response

import com.dicoding.storyapp.data.entity.StoryEntity
import com.google.gson.annotations.SerializedName

data class AllStoriesResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<StoryEntity>
)