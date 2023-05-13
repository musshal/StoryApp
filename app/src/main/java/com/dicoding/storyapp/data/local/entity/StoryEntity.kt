package com.dicoding.storyapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stories")
class StoryEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    val id: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "description")
    val description: String,

    @field:ColumnInfo(name = "photoUrl")
    val photoUrl: String,

    @field:ColumnInfo(name = "createdAt")
    val createdAt: String,

    @field:SerializedName("lat")
    val lat: Float? = null,

    @field:SerializedName("lon")
    val lon: Float? = null
)