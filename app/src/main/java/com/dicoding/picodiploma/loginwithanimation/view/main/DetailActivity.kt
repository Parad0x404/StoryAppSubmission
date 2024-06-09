package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Detail Story"

        getDetailData()
    }

    private fun getDetailData() {
        val storyBanner = intent.getStringExtra("banner")
        val storyTitle = intent.getStringExtra("title")
        val storyDesc = intent.getStringExtra("desc")

        Glide.with(applicationContext)
            .load(storyBanner)
            .transform(RoundedCorners(10))
            .into(binding.ivDetailStory)

        binding.tvDetailTitle.text = storyTitle
        binding.tvDetailDesc.text = storyDesc
    }
}