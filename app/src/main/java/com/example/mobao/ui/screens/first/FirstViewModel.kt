package com.example.mobao.ui.screens.first

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobao.data.model.Post
import com.example.mobao.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            postRepository.getPosts().collect {
                _posts.value = it
            }
        }
    }

    fun addPost(title: String, content: String, author: String) {
        viewModelScope.launch {
            val newPost = Post(
                title = title,
                content = content,
                author = author,
            )
            // Firebase에 게시글 추가
            postRepository.addPost(newPost)
        }
    }
}