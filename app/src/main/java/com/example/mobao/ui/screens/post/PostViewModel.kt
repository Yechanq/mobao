package com.example.mobao.ui.screens.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobao.data.model.Post
import com.example.mobao.data.repository.PostRepository // PostRepository import
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository // PostRepository 주입
) : ViewModel() {

    fun savePost(title: String, content: String, author: String = "익명") {
        viewModelScope.launch {
            val newPost = Post(title = title, content = content, author = author)
            val success = postRepository.addPost(newPost)
            if (success) {
                println("게시글 저장 성공!")
                // 저장 성공 후 필요한 작업 (예: FirstScreen으로 돌아가기)
            } else {
                println("게시글 저장 실패!")
            }
        }
    }
}