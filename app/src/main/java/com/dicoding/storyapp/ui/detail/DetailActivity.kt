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
import com.dicoding.storyapp.utils.DateFormatter

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

        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = intent.getParcelableExtra(EXTRA_STORY) as StoryEntity?

        if (story != null) {
            setupViewModel()
            setupData(story)
            setupAction(story)
        }
    }

    private fun setupAction(story: StoryEntity) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            setupData(story)
        }
    }

    private fun setupData(story: StoryEntity) {
        fabBookmarkAction(story)

        viewModel.getLogin().observe(this) { user ->
            executeGetDetailStory(user.token, story.id)
        }

        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun executeGetDetailStory(token: String, id: String) {
        viewModel.getDetailStory(token, id).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvMessage.visibility = View.GONE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvMessage.visibility = View.GONE
                        binding.fabDetailSaveBookmark.visibility = View.VISIBLE

                        setData(result.data.story)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvMessage.visibility = View.VISIBLE
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
            tvDetailCreatedAt.text = DateFormatter.formatDate(story.createdAt)
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
            R.id.menu_sign_out -> {
                showLogoutDialog()
                true
            }
            else -> true
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sign_out)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.deleteLogin()
                directToMainActivity()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
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