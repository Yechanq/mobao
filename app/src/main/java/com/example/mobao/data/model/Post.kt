package com.example.mobao.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.Timestamp

data class Post(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val author: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null
)