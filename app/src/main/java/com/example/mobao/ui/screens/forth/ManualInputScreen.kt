package com.example.mobao.ui.screens.forth

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputScreen(
    navController: NavController,
    viewModel: ForthMainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var countText by remember { mutableStateOf("") }
    var times by remember { mutableStateOf(mutableListOf<LocalTime>()) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("약 수동 추가") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("약 이름") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = countText,
                onValueChange = { newVal -> countText = newVal.filter { it.isDigit() } },
                label = { Text("약 개수 (선택)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text("알림 시간", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (times.isEmpty()) {
                Text(
                    "알림 시간이 없습니다. 아래 버튼을 눌러 추가하세요.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn {
                    itemsIndexed(times) { index, time ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(time.format(timeFormatter), style = MaterialTheme.typography.bodyLarge)
                            Button(onClick = { times.removeAt(index) }) {
                                Text("삭제")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        times.add(LocalTime.of(hour, minute))
                    },
                    LocalTime.now().hour,
                    LocalTime.now().minute,
                    true
                ).show()
            }) {
                Text("알림 시간 추가")
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val count = countText.toIntOrNull()
                        // DB 저장 및 알림 스케줄링
                        viewModel.insertMedicineWithTimes(name, times.toList(), count)
                        // 메인 화면으로 이동
                        navController.navigate("forthMain") {
                            popUpTo("first") { inclusive = false }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("약 추가하기")
            }
        }
    }
}
