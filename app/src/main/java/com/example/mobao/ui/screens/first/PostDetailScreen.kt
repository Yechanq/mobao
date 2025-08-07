package com.example.mobao.ui.screens.first

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(postId) {
        viewModel.getPost(postId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.post != null -> {
                val post = uiState.post
                Column(modifier = Modifier.fillMaxSize()) {
                    if (post != null) {
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (post != null) {
                        Text(
                            text = "작성자: ${post.author}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (post != null) {
                        Text(
                            text = post.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            uiState.error != null -> {
                Text(text = "오류: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                Text(text = "게시글을 찾을 수 없습니다.")
            }
        }
    }
}