package com.dicoding.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.storyapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(story: StoryEntity)

    @Delete
    fun delete(story: StoryEntity)

    @Update
    fun update(story: StoryEntity)

    @Query("SELECT * FROM stories")
    fun getAllStories(): LiveData<ArrayList<StoryEntity>>
}