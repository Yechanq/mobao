package com.example.mobao.data.repository

import com.example.mobao.data.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun addPost(post: Post): Boolean
    fun getPosts(): Flow<List<Post>>
    // 필요한 경우 다른 CRUD 작업 추가 (업데이트, 삭제 등)
}