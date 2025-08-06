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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController // NavHostController import 추가
import com.example.mobao.data.model.Post

@Composable
fun FirstScreen(
    // NavHostController를 파라미터로 추가합니다.
    navController: NavHostController,
    viewModel: FirstViewModel = hiltViewModel()
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
            // "post" 라우트로 이동합니다.
            navController.navigate("post")
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

    }
}