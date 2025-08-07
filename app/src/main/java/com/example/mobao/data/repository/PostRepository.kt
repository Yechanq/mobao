package com.example.mobao.data.repository

import com.example.mobao.data.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun addPost(post: Post): Boolean
    fun getPosts(): Flow<List<Post>>
}