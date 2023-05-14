package com.dicoding.storyapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.databinding.FragmentHomeBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.adapter.StoriesAdapter

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    private var backPressedTime: Long = 0
    private val BACK_PRESSED_INTERVAL = 2000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupViewModel()
        setupData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            myUpdateOperation()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]
    }

    private fun setupData() {
        viewModel.getLogin().observe(viewLifecycleOwner) { user ->
            if (user.token.isNotBlank()) {
                executeGetAllStories(user.token)
            }
        }

        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun executeGetAllStories(token: String) {
        viewModel.getAllStories(token).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        setData(result.data.listStory)
                    }
                    is Result.Error -> {
                        Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setData(listStory: ArrayList<StoryResponse>) {
        binding.rvStories.adapter = StoriesAdapter(listStory)
        binding.rvStories.layoutManager = LinearLayoutManager(context)
    }

    private fun myUpdateOperation() {
        setupData()
    }

    private fun setupAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + BACK_PRESSED_INTERVAL > System.currentTimeMillis()) {
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }
}