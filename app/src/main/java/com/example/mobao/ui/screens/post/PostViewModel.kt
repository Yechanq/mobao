package com.example.mobao.ui.screens.post

import androidx.lifecycle.ViewModel
import com.example.mobao.data.model.Post
import com.example.mobao.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    suspend fun savePost(title: String, content: String, author: String = "익명"): Boolean {
        val newPost = Post(
            title = title,
            content = content,
            author = author,
        )
        val success = postRepository.addPost(newPost)
        if (success) {
            println("게시글이 Firebase에 성공적으로 저장되었습니다!")
        } else {
            println("게시글 저장에 실패했습니다!")
        }
        return success
    }
}