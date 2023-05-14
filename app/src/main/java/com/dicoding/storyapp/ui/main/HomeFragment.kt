package com.dicoding.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.databinding.FragmentHomeBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.adapter.StoriesAdapter
import com.dicoding.storyapp.ui.bookmark.BookmarkActivity
import com.dicoding.storyapp.ui.insert.InsertActivity
import com.dicoding.storyapp.ui.setting.SettingActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private var backPressedTime: Long = 0
    private val BACK_PRESSED_INTERVAL = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + BACK_PRESSED_INTERVAL > System.currentTimeMillis()) {
                    // If back button is clicked twice within the time interval, close the app or fragment
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }

    private fun setupData() {
        viewModel.getLogin().observe(viewLifecycleOwner) { user ->
            if (user.token.isNotBlank()) {
                viewModel.getAllStories(user.token).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.rvStories.adapter = StoriesAdapter(result.data.listStory)
                                binding.rvStories.layoutManager = LinearLayoutManager(context)
                            }
                            is Result.Error -> {
                                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[MainViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu_2, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_insert -> {
                startActivity(Intent(context, InsertActivity::class.java))
                true
            }
            R.id.menu_bookmarks -> {
                startActivity(Intent(context, BookmarkActivity::class.java))
                true
            }
            R.id.menu_setting -> {
                startActivity(Intent(context, SettingActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                showLogoutDialog()
                true
            }
            else -> true
        }
    }

    private fun myUpdateOperation() {
        setupData()
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
            .setMessage("Are you serious?")
            .setPositiveButton("OK") { _, _ ->
                run {
                    viewModel.deleteLogin()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                run {
                    dialog.dismiss()
                }
            }

        val alert = builder.create()
        alert.show()
    }
}