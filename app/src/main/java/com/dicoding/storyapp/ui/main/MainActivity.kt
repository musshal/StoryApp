package com.dicoding.storyapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.helper.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupFragment()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[MainViewModel::class.java]
    }

    private fun setupFragment() {
        viewModel.getLogin().observe(this) { user ->
            if (user.token.isNotBlank()) {
                addHomeFragment()
            } else {
                addLoginFragment()
            }
        }
    }

    private fun addHomeFragment() {
        val fragmentManager = supportFragmentManager
        val homeFragment = HomeFragment()
        val fragment = fragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)

        if (fragment !is HomeFragment) {
            Log.d("StoryApp", "Fragment Name: ${HomeFragment::class.java.simpleName}")
            fragmentManager
                .beginTransaction()
                .replace(R.id.frame_container, homeFragment, HomeFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun addLoginFragment() {
        val fragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val fragment = fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

        if (fragment !is LoginFragment) {
            Log.d("StoryApp", "Fragment Name: ${LoginFragment::class.java.simpleName}")
            fragmentManager
                .beginTransaction()
                .replace(R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName)
                .commit()
        }
    }
}