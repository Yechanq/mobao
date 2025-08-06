package com.example.mobao.ui.screens.post

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme // MaterialTheme 임포트 추가
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navController: NavHostController, // NavHostController를 파라미터로 받습니다.
    viewModel: PostViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // CoroutineScope를 생성하여 버튼 클릭 시 비동기 작업을 시작하고 관리합니다.
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "새 게시글 작성", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("제목") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("내용") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남은 공간을 채우도록 설정
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // CoroutineScope 내에서 비동기 작업을 시작합니다.
                coroutineScope.launch {
                    // viewModel.savePost()는 suspend 함수이므로, 완료될 때까지 기다립니다.
                    val success = viewModel.savePost(title, content)
                    if (success) {
                        // 게시글 저장 성공 시에만 이전 화면으로 돌아갑니다.
                        navController.popBackStack()
                    } else {
                        // TODO: 저장 실패 시 사용자에게 알림을 표시하는 로직을 추가할 수 있습니다.
                        // 예: Toast.makeText(context, "게시글 저장 실패", Toast.LENGTH_SHORT).show()
                        println("게시글 저장에 실패했습니다. 다시 시도해주세요.")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "게시글 저장")
        }
    }
}
