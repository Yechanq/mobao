package com.example.mobao.ui.screens.first

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// 아래 import를 변경해야 합니다.
// import androidx.lifecycle.viewmodel.compose.viewModel // 이 줄을 삭제하거나 주석 처리하세요.
import androidx.hilt.navigation.compose.hiltViewModel // 이 줄을 추가하세요.
import com.example.mobao.data.model.Post

@Composable
fun FirstScreen(
    // 이 부분을 변경해야 합니다.
    viewModel: FirstViewModel = hiltViewModel() // viewModel() 대신 hiltViewModel() 사용
) {
    // ViewModel의 posts StateFlow를 관찰합니다.
    val posts by viewModel.posts.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "커뮤니티 게시판", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // 게시글 목록을 표시
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(posts) { post ->
                PostItem(post = post)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 게시글 추가 버튼
        Button(onClick = {
            // 예시: 새 게시글 추가 로직
            viewModel.addPost("새로운 글 제목", "새로운 글 내용", "작성자 이름")
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "게시글 작성")
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.content, style = MaterialTheme.typography.bodyMedium)
            Text(text = "작성자: ${post.author}", style = MaterialTheme.typography.bodySmall)
        }
    }
}