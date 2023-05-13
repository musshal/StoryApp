package com.dicoding.storyapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.local.preferences.UserPreferences
import com.dicoding.storyapp.databinding.ActivityHomeBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
    }

    private fun setupViewModel() {
        val userPreferences = UserPreferences.getInstance(dataStore)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(userPreferences)
        )[HomeViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_3, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                showLogoutDialog()
                true
            }

            else -> true
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
            .setMessage("Are you serious?")
            .setPositiveButton("OK") { dialog, _ ->
                run {
                    viewModel.deleteLogin()
                    moveToHomeActivity()
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

    private fun moveToHomeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}