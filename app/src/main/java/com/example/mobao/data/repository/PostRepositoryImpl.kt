package com.example.mobao.data.repository

import com.example.mobao.data.model.Post
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject // 이 import를 추가하세요

// @Inject constructor를 추가하여 Hilt가 이 클래스를 인스턴스화할 수 있게 합니다.
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {

    override fun getPosts(): Flow<List<Post>> = flow {
        val querySnapshot = firestore.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
        val posts = querySnapshot.toObjects(Post::class.java)
        emit(posts)
    }

    override suspend fun addPost(post: Post): DocumentReference {
        return firestore.collection("posts").add(post).await()
    }

    override suspend fun updatePost(post: Post) {
        firestore.collection("posts").document(post.id).set(post).await()
    }

    override suspend fun deletePost(postId: String) {
        firestore.collection("posts").document(postId).delete().await()
    }
}