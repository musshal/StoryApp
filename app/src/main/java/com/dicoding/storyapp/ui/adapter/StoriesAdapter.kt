package com.dicoding.storyapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.response.StoryResponse

class StoriesAdapter(private val stories: ArrayList<StoryResponse>) :
    RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivStoryImage: ImageView = view.findViewById(R.id.iv_story_image)
        val tvStoryName: TextView = view.findViewById(R.id.tv_story_name)
        val tvStoryDescription: TextView = view.findViewById(R.id.tv_story_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_row_story,
            parent,
            false)
        )

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = stories[position]

        Glide.with(holder.itemView.context).load(story.photoUrl).into(holder.ivStoryImage)

        holder.tvStoryName.text = story.name
        holder.tvStoryDescription.text = story.description
    }
}