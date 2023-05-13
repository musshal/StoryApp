package com.dicoding.storyapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.storyapp.data.local.entity.StoryEntity

@Database(entities = [StoryEntity::class], version = 1)
abstract class StoryRoomDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private var INSTANCE: StoryRoomDatabase? = null
        @JvmStatic
        fun getInstance(context: Context): StoryRoomDatabase {
            if (INSTANCE == null) {
                synchronized(StoryRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        StoryRoomDatabase::class.java, "stories_db")
                        .build()
                }
            }

            return INSTANCE as StoryRoomDatabase
        }
    }
}