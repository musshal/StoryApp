package com.dicoding.storyapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.source.local.entity.StoryEntity
import com.dicoding.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.storyapp.ui.detail.DetailActivity

class StoriesAdapter(private val onBookmarkClick: (StoryEntity) -> Unit) : ListAdapter<StoryEntity, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivStoryImage = binding.ivItemPhoto
        val tvStoryName = binding.tvItemName
        val tvStoryDescription = binding.tvItemDescription
        val ivBookmark = binding.ivBookmark

        fun bind(story: StoryEntity) {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STORY, story)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)

        Glide.with(holder.itemView.context).load(story.photoUrl).into(holder.ivStoryImage)

        holder.tvStoryName.text = story.name
        holder.tvStoryDescription.text = story.description

        if (story.isBookmarked) {
            holder.ivBookmark.setImageDrawable(ContextCompat.getDrawable(holder.ivBookmark.context, R.drawable.baseline_bookmark_24))
        } else {
            holder.ivBookmark.setImageDrawable(ContextCompat.getDrawable(holder.ivBookmark.context, R.drawable.baseline_bookmark_border_24))
        }

        holder.ivBookmark.setOnClickListener {
            onBookmarkClick(story)
        }

        holder.bind(story)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}