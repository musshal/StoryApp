package com.dicoding.storyapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.databinding.FragmentBookmarkBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.adapter.StoriesAdapter

class BookmarkFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var storiesAdapter: StoriesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupViewModel()
        setupData()
        setData()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]
    }

    private fun setupAdapter() {
        storiesAdapter = StoriesAdapter { story ->
            if (story.isBookmarked) {
                viewModel.deleteStory(story)
            } else {
                viewModel.saveStory(story)
            }
        }
    }

    private fun setupData() {
        viewModel.getBookmarkedStories().observe(viewLifecycleOwner) { bookmarkedStory ->
            if (bookmarkedStory.isEmpty()) {
                binding.tvMessage.visibility = View.VISIBLE
                storiesAdapter.submitList(bookmarkedStory)
            } else {
                binding.tvMessage.visibility = View.GONE
                storiesAdapter.submitList(bookmarkedStory)
            }
        }
    }

    private fun setData() {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storiesAdapter
        }
    }
}