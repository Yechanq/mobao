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
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // 1) 로컬 상태 선언 (초기값은 빈 상태)
    var countText by remember { mutableStateOf("") }
    var times by remember { mutableStateOf(mutableListOf<LocalTime>()) }

    // 2) medicine 객체가 변경될 때마다 countText, times 갱신
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
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
                itemsIndexed(times) { idx, t ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(t.format(timeFormatter))
                        Row {
                            IconButton(onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hr, min ->
                                        times[idx] = LocalTime.of(hr, min)
                                    },
                                    t.hour, t.minute, true
                                ).show()
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "수정")
                            }
                            IconButton(onClick = { times.removeAt(idx) }) {
                                Icon(Icons.Default.Delete, contentDescription = "삭제")
                            }
                        }
                    }
                }
            }
            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hr, min -> times.add(LocalTime.of(hr, min)) },
                    LocalTime.now().hour, LocalTime.now().minute, true
                ).show()
            }) {
                Text("시간 추가")
            }

            Spacer(Modifier.weight(1f))

            // 저장 & 삭제 버튼
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    onClick = {
                        viewModel.deleteMedicine()
                        navController.popBackStack()
                    }
                ) {
                    Text("삭제")
                }
            }
        }
    }
}
