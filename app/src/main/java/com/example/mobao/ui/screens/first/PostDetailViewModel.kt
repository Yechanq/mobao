package com.example.mobao.ui.screens.first

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobao.data.model.Post
import com.example.mobao.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI 상태를 관리하기 위한 data class
data class PostDetailUiState(
    val post: Post? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState

    fun getPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val post = postRepository.getPostById(postId) // repository에서 게시글 가져오기
                _uiState.value = _uiState.value.copy(post = post, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "게시글을 불러오는 데 실패했습니다: ${e.message}"
                )
            }
        }
    }
}