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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.mobao.data.model.Post

@Composable
fun FirstScreen(
    navController: NavHostController,
    viewModel: FirstViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()

    // Box를 사용하여 버튼과 다른 UI 요소를 겹치거나 특정 위치에 배치할 수 있습니다.
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) { // 전체 화면 패딩 적용
        Column(modifier = Modifier.fillMaxSize()) { // 기존 Column은 Box의 자식으로 전체 공간 차지
            Text(text = "노인 갤러리", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (posts.isEmpty()) {
                    item {
                        Text(text = "게시글이 없습니다. 새 게시글을 작성해보세요!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    items(posts) { post ->
                        PostItem(post = post)
                    }
                }
            }
        }

        // "게시글 작성" 버튼을 Box 내에서 우측 상단에 배치합니다.
        Button(
            onClick = {
                navController.navigate("post")
            },
            modifier = Modifier
                .align(Alignment.TopEnd) // Box 내에서 우측 상단에 정렬
                .padding(top = 0.dp, end = 0.dp) // Box에 이미 패딩이 있으므로 추가 패딩은 0으로 설정
        ) {
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
            // 제목과 작성자를 한 줄에 배치하고 작성자를 오른쪽 끝에 정렬합니다.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // 가로 공간을 균등하게 배분
                verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
            ) {
                Text(text = post.title, style = MaterialTheme.typography.headlineSmall)
                if (post.author.isNotBlank()) {
                    Text(text = "작성자: ${post.author}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}