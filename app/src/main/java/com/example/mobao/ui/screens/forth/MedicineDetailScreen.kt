package com.example.mobao.ui.screens.forth

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    navController: NavController,
    viewModel: MedicineDetailViewModel = hiltViewModel()
) {
    val medicine by viewModel.medicine.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    if (medicine == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 1) 로컬 상태 선언
    var countText by remember { mutableStateOf("") }
    var times by remember { mutableStateOf(mutableListOf<LocalTime>()) }

    // 2) medicine가 바뀔 때마다 상태 갱신
    LaunchedEffect(medicine) {
        countText = medicine!!.remainingPillCount?.toString() ?: ""
        times = medicine!!.reminderTimes.toMutableList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(medicine!!.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "뒤로")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 남은 개수 입력
            OutlinedTextField(
                value = countText,
                onValueChange = { countText = it.filter(Char::isDigit) },
                label = { Text("남은 개수") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // 복용 시간 리스트
            Text("복용 시간", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                itemsIndexed(times) { idx, time ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(time.format(timeFormatter))
                        Row {
                            IconButton(onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val newList = times.toMutableList()
                                        newList[idx] = LocalTime.of(hour, minute)
                                        times = newList
                                    },
                                    time.hour, time.minute, true
                                ).show()
                            }) {
                                Icon(Icons.Default.Edit, "수정")
                            }
                            IconButton(onClick = {
                                // 삭제 시 snapshot 상태로 새 리스트 할당
                                times = times.filterIndexed { i, _ -> i != idx }
                                    .toMutableList()
                            }) {
                                Icon(Icons.Default.Delete, "삭제")
                            }
                        }
                    }
                }
            }

            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val newList = times.toMutableList()
                        newList.add(LocalTime.of(hour, minute))
                        times = newList
                    },
                    LocalTime.now().hour,
                    LocalTime.now().minute,
                    true
                ).show()
            }) {
                Text("시간 추가")
            }

            Spacer(modifier = Modifier.weight(1f))

            // 저장 & 삭제 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    viewModel.saveMedicineDetails(
                        countText.toIntOrNull(),
                        times
                    )
                    navController.popBackStack()
                }) {
                    Text("저장")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.deleteMedicine()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("삭제")
                }
            }
        }
    }
}
