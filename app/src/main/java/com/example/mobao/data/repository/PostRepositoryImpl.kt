package com.example.mobao.data.repository

import com.example.mobao.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {

    private val postsCollection = firestore.collection("posts")

    override suspend fun addPost(post: Post): Boolean {
        return try {
            postsCollection.add(post).await()
            true
        } catch (e: Exception) {
            println("Error adding post to Firestore: ${e.message}")
            false
        }
    }

    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        val subscription = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts)
            }

        awaitClose { subscription.remove() }
    }

}
