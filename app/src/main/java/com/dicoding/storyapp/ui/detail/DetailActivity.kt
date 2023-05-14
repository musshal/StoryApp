package com.dicoding.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.helper.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = intent.getParcelableExtra(EXTRA_STORY) as StoryResponse?

        setupViewModel()
        setupAction(story)
    }

    private fun setupAction(story: StoryResponse?) {
        viewModel.getLogin().observe(this) { user ->
            if (story != null) {
                viewModel.getDetailStory(user.token, story.id).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val storyDetail = result.data.story

                                Glide.with(this@DetailActivity).load(storyDetail.photoUrl).into(binding.ivDetailPhoto)
                                binding.tvDetailName.text = storyDetail.name
                                binding.tvDetailDescription.text = storyDetail.description
                                binding.tvDetailCreatedAt.text = storyDetail.createdAt
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[DetailViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_3, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
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
            .setPositiveButton("OK") { _, _ ->
                run {
                    viewModel.deleteLogin()
                    finish()
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