package com.dicoding.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.data.entity.StoryEntity
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.ui.adapter.StoriesHomeAdapter
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.MainDispatcherRule
import com.dicoding.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences
    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var viewModel: HomeViewModel
    private lateinit var token: String


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = HomeViewModel(userPreferences, storyRepository)
        token = "dummyToken"
    }

    @Test
    fun `when get all stories should not null and return data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryEntity()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        `when`(storyRepository.getAllStories(token)).thenReturn(expectedStory)
        val actualStory: PagingData<StoryEntity> = viewModel.getAllStories(token).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesHomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when get all stories should return no data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        `when`(storyRepository.getAllStories(token)).thenReturn(expectedStory)
        val actualStory: PagingData<StoryEntity> = viewModel.getAllStories(token).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesHomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
        companion object {
            fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
                return PagingData.from(items)
            }
        }

        @Suppress("SameReturnValue")
        override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}