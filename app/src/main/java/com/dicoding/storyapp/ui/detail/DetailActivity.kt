package com.dicoding.storyapp.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.source.local.entity.StoryEntity
import com.dicoding.storyapp.data.source.remote.response.StoryResponse
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.main.MainActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    private val story = intent.getParcelableExtra(EXTRA_STORY) as StoryEntity?

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        setupAction()
    }

    private fun setupAction() {
        if (story != null) {
            fabBookmarkAction(story)

            viewModel.getLogin().observe(this) { user ->
                executeGetDetailStory(user.token, story.id)
            }
        }
    }

    private fun executeGetDetailStory(token: String, id: String) {
        viewModel.getDetailStory(token, id).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.fabDetailSaveBookmark.visibility = View.VISIBLE

                        setData(result.data.story)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun fabBookmarkAction(story: StoryEntity) {
        binding.apply {
            if (story.isBookmarked) {
                fabDetailSaveBookmark.setImageDrawable(ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.baseline_bookmark_48
                ))
            } else {
                fabDetailSaveBookmark.setImageDrawable(ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.baseline_bookmark_border_48
                ))
            }

            fabDetailSaveBookmark.setOnClickListener {
                if (story.isBookmarked) {
                    viewModel.deleteStory(story)
                    fabDetailSaveBookmark.setImageDrawable(ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.baseline_bookmark_border_48
                    ))
                } else {
                    viewModel.saveStory(story)
                    fabDetailSaveBookmark.setImageDrawable(ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.baseline_bookmark_48
                    ))
                }
            }
        }
    }

    private fun setData(story: StoryResponse) {
        binding.apply {
            Glide
                .with(this@DetailActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)

            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            tvDetailCreatedAt.text = story.createdAt
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
                viewModel.deleteLogin()
                directToMainActivity()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun directToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}