package com.dicoding.storyapp.ui.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.local.preferences.UserPreference
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.home.HomeFragment
import com.dicoding.storyapp.ui.login.LoginFragment

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

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
        viewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[MainViewModel::class.java]
    }

    private fun setupFragment() {
        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            val fragmentManager = supportFragmentManager
            if (isLoggedIn) {
                val homeFragment = HomeFragment()
                val fragment = fragmentManager
                    .findFragmentByTag(HomeFragment::class.java.simpleName)

                if (fragment !is LoginFragment) {
                    Log.d("StoryApp", "Fragment Name: " + HomeFragment::class.java.simpleName)
                    fragmentManager
                        .beginTransaction()
                        .add(
                            R.id.frame_container,
                            homeFragment,
                            HomeFragment::class.java.simpleName
                        )
                        .commit()
                }
            } else {
                val loginFragment = LoginFragment()
                val fragment = fragmentManager
                    .findFragmentByTag(LoginFragment::class.java.simpleName)

                if (fragment !is LoginFragment) {
                    Log.d("StoryApp", "Fragment Name: " + LoginFragment::class.java.simpleName)
                    fragmentManager
                        .beginTransaction()
                        .add(
                            R.id.frame_container,
                            loginFragment,
                            LoginFragment::class.java.simpleName
                        )
                        .commit()
                }
            }
        }
    }
}