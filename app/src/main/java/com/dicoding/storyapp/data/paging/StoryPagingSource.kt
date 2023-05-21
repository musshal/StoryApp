package com.dicoding.storyapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.data.source.local.entity.StoryEntity
import com.dicoding.storyapp.data.source.local.room.StoryDao
import com.dicoding.storyapp.data.source.remote.retrofit.ApiService
import java.lang.Exception

class StoryPagingSource(
    private val apiService: ApiService,
    private val storyDao: StoryDao,
    private val token: String
    ): PagingSource<Int, StoryEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, StoryEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryEntity> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(
                "Bearer $token",
                position,
                params.loadSize
            )
            val data = responseData.listStory
            val storyList = data.map { story ->
                val isBookmarked = storyDao.isStoryBookmarked(story.id)
                StoryEntity(
                    story.id,
                    story.name,
                    story.description,
                    story.photoUrl,
                    story.createdAt,
                    story.lat,
                    story.lon,
                    isBookmarked
                )
            }
            storyDao.deleteAll()
            storyDao.insertStory(storyList)
            val localData = storyDao.getAllStories()
            LoadResult.Page(
                data = localData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}