package com.example.mobao.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.Timestamp // Timestamp를 사용하려면 이 임포트가 필요합니다.

data class Post(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val author: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null // 수정된 부분: Long 대신 Timestamp를 사용하고, nullable로 변경
)