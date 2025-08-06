package com.example.mobao.data.repository

import com.example.mobao.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {

    private val postsCollection = firestore.collection("posts") // "posts" 컬렉션 사용

    override suspend fun addPost(post: Post): Boolean {
        return try {
            // Firestore에 문서 추가. ID는 Firestore에서 자동으로 생성
            postsCollection.add(post).await()
            true
        } catch (e: Exception) {
            println("Error adding post: ${e.message}")
            false
        }
    }

    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        val subscription = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING) // 최신 글부터 정렬
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // 에러 발생 시 Flow 닫기
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id) // 문서 ID도 Post 객체에 포함
                } ?: emptyList()
                trySend(posts) // 새로운 데이터 발행
            }

        awaitClose { subscription.remove() } // Flow가 닫힐 때 리스너 제거
    }
}