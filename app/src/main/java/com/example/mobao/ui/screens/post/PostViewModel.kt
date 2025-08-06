package com.example.mobao.ui.screens.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobao.data.model.Post
import com.example.mobao.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    // savePost 함수를 suspend 함수로 만들고 Boolean 값을 직접 반환하도록 수정합니다.
    // 이렇게 하면 이 함수를 호출하는 쪽에서 작업 완료를 기다릴 수 있습니다.
    suspend fun savePost(title: String, content: String, author: String = "익명"): Boolean {
        val newPost = Post(
            title = title,
            content = content,
            author = author,
        )
        val success = postRepository.addPost(newPost) // Repository의 addPost 호출 및 결과 받기
        if (success) {
            println("게시글이 Firebase에 성공적으로 저장되었습니다!")
        } else {
            println("게시글 저장에 실패했습니다!")
        }
        return success // 저장 성공 여부를 반환합니다.
    }
}