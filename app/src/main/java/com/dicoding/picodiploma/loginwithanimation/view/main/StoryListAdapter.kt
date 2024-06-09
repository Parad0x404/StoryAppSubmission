package com.dicoding.picodiploma.loginwithanimation.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowStoryBinding

class StoryListAdapter : PagingDataAdapter<ListStoryItem, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            val bannerIV = binding.ivStory
            val titleTV = binding.tvTitle
            val descTV = binding.tvDescription

            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(bannerIV)
            titleTV.text = data.name
            descTV.text = data.description

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra("banner", data.photoUrl)
                intent.putExtra("title", data.name)
                intent.putExtra("desc", data.description)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(bannerIV, "image"),
                        Pair(titleTV, "name"),
                        Pair(descTV, "description")
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}