package com.dicoding.storyapp

import com.dicoding.storyapp.data.lib.story.Story

object DataDummy {

    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "created + $i",
                "description $i",
                0.0,
                0.0,
                "names $i",
                "url $i"
            )
            items.add(story)
        }
        return items
    }
}