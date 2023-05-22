package com.dicoding.storyapp.utils

import com.dicoding.storyapp.data.entity.StoryEntity

object DataDummy {
    fun generateDummyStoryEntity(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                "story-$i",
                "John Doe",
                "An image description.",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                0.0,
                0.0,
                false
            )
            items.add(story)
        }
        return items
    }
}