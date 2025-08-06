package com.example.mobao.data.repository

import com.example.mobao.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore // FirebaseFirestore 임포트
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await // await() 함수를 사용하기 위한 임포트
import javax.inject.Inject
import javax.inject.Singleton // Singleton 어노테이션 추가

// Hilt를 사용하여 주입 가능하도록 @Singleton 어노테이션 추가
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore // FirebaseFirestore 인스턴스 주입
) : PostRepository {

    // Firestore의 "posts" 컬렉션을 참조합니다.
    // 이 "posts"는 Firebase Firestore 콘솔에서 생성될 컬렉션 이름입니다.
    private val postsCollection = firestore.collection("posts")

    // 새로운 게시글을 Firestore에 추가하는 함수
    override suspend fun addPost(post: Post): Boolean {
        return try {
            // add() 함수는 Firestore에 새 문서를 추가하고, 문서 ID를 자동으로 생성합니다.
            // await()을 사용하여 비동기 작업이 완료될 때까지 기다립니다.
            postsCollection.add(post).await()
            true // 성공 시 true 반환
        } catch (e: Exception) {
            // 에러 발생 시 로그 출력 및 false 반환
            println("Error adding post to Firestore: ${e.message}")
            false
        }
    }

    // Firestore에서 게시글 목록을 실시간으로 가져오는 함수 (Flow 사용)
    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        // addSnapshotListener를 사용하여 "posts" 컬렉션의 변경 사항을 실시간으로 받습니다.
        val subscription = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING) // 'timestamp' 필드를 기준으로 최신 글부터 정렬
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // 에러 발생 시 Flow를 닫습니다.
                    return@addSnapshotListener
                }

                // 스냅샷의 문서들을 Post 객체로 변환합니다.
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    // 문서 ID도 Post 객체에 포함시키기 위해 copy() 사용 (Post 데이터 클래스에 id 필드가 있어야 함)
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts) // 변환된 게시글 목록을 Flow에 발행합니다.
            }

        // Flow가 취소될 때 Firestore 리스너를 제거합니다. (메모리 누수 방지)
        awaitClose { subscription.remove() }
    }
}
