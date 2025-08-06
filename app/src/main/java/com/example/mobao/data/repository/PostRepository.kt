package com.example.mobao.data.repository

import com.google.firebase.firestore.DocumentReference
import com.example.mobao.data.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPosts(): Flow<List<Post>>
    suspend fun addPost(post: Post): DocumentReference
    suspend fun updatePost(post: Post)
    suspend fun deletePost(postId: String)
}