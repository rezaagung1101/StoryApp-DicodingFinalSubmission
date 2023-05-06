package com.dicoding.storyapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.lib.story.Story
import com.dicoding.storyapp.databinding.CardStoryBinding
import com.dicoding.storyapp.ui.detail.DetailActivity

class StoryPagingAdapter : PagingDataAdapter<Story, StoryPagingAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class ViewHolder(private val binding: CardStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(story: Story) {
            with(binding) {
                imgItemPhoto.setImageResource(R.drawable.avatar)
                tvItemName.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(imgItemStory)
                itemView.setOnClickListener {
                    val intentToDetail = Intent(itemView.context, DetailActivity::class.java)
                    intentToDetail.putExtra("NAME", story.name)
                    intentToDetail.putExtra("CREATEDAt", story.createdAt)
                    intentToDetail.putExtra("DESCRIPTION", story.description)
                    intentToDetail.putExtra("PHOTOURL", story.photoUrl)
                    itemView.context.startActivity(intentToDetail)
                    }

                }
            }
        }

}