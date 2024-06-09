package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "https://example.com/image$i.jpg",
                createdAt = "2023-01-$i",
                name = "Story $i",
                description = "This is story number $i",
                lon = 100.0 + i,
                id = i.toString(),
                lat = 0.0 + i
            )
            items.add(story)
        }
        return items
    }
}