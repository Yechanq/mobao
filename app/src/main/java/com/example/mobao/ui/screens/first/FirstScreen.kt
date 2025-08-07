package com.example.mobao.ui.screens.first

import androidx.compose.foundation.clickable
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
import com.example.mobao.ui.screens.first.PostItem // 추가된 PostItem import

@Composable
fun FirstScreen(
    navController: NavHostController,
    viewModel: FirstViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "노인 갤러리", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (posts.isEmpty()) {
                    item {
                        Text(
                            text = "게시글이 없습니다. 새 게시글을 작성해보세요!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(posts) { post ->
                        PostItem(
                            post = post,
                            // 게시글 아이템 클릭 시, PostDetailScreen으로 이동하는 로직 추가
                            onClick = { navController.navigate("postDetail/${post.id}") }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                navController.navigate("post")
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 0.dp)
        ) {
            Text(text = "게시글 작성")
        }
    }
}

@Composable
fun PostItem(post: Post, onClick: () -> Unit) { // onClick 매개변수 추가
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick) // clickable 모디파이어 추가
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = post.title, style = MaterialTheme.typography.headlineSmall)
                if (post.author.isNotBlank()) {
                    Text(text = "작성자: ${post.author}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}