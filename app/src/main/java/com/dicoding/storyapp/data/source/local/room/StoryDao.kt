package com.dicoding.storyapp.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dicoding.storyapp.data.source.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStories(): List<StoryEntity>

    @Query("SELECT * FROM stories WHERE id = :id")
    fun getDetailStory(id: String): LiveData<StoryEntity>

    @Query("SELECT * FROM stories where isBookmarked = 1")
    fun getBookmarkedStories(): LiveData<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStory(stories: List<StoryEntity>)

    @Update
    fun updateStory(stories: StoryEntity)

    @Query("DELETE FROM stories WHERE isBookmarked = 0")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM stories WHERE id = :id AND isBookmarked = 1)")
    fun isStoryBookmarked(id: String): Boolean
}