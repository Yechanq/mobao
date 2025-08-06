package com.example.mobao.ui.screens.forth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mobao.data.model.Medicine
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForthMainScreen(
    navController: NavController,
    viewModel: ForthMainViewModel
) {
    val allMedicines by viewModel.medicines.collectAsStateWithLifecycle(initialValue = emptyList())
    val activeMedicines = allMedicines.filter { it.remainingPillCount == null || it.remainingPillCount > 0 }
    val completedMedicines = allMedicines.filter { it.remainingPillCount != null && it.remainingPillCount == 0 }

    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var completedVisible by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "복용 중인 약 (${activeMedicines.size})", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            if (activeMedicines.isEmpty()) {
                Text("복용 중인 약이 없습니다.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(activeMedicines) { medicine: Medicine ->
                        MedicineItem(medicine) {
                            selectedMedicine = medicine
                            showDialog = true
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "복용 완료한 약 (${completedMedicines.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable { completedVisible = !completedVisible }
            )
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(visible = completedVisible) {
                if (completedMedicines.isEmpty()) {
                    Text("복용 완료한 약이 없습니다.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(completedMedicines) { medicine: Medicine ->
                            MedicineItem(medicine) {
                                selectedMedicine = medicine
                                showDialog = true
                            }
                        }
                    }
                }
            }
        }
    }

    // 약 수정/삭제 다이얼로그
    if (showDialog && selectedMedicine != null) {
        MedicineEditDialog(
            medicine = selectedMedicine!!,
            onDismiss = { showDialog = false },
            onUpdate = { newCount ->
                viewModel.updateMedicineCount(selectedMedicine!!, newCount)
                showDialog = false
            },
            onDelete = {
                viewModel.deleteMedicine(selectedMedicine!!)
                showDialog = false
            }
        )
    }

    // 약 추가하기 BottomSheet
    if (showAddSheet) {
        ModalBottomSheet(onDismissRequest = { showAddSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("약 추가 방법 선택", style = MaterialTheme.typography.titleMedium)

                Button(onClick = {
                    showAddSheet = false
                    navController.navigate("addMedicine") // 카메라/갤러리 OCR 화면
                }) {
                    Text("카메라로 추가")
                }

                Button(onClick = {
                    showAddSheet = false
                    navController.navigate("addMedicine") // addMedicine 화면에서 갤러리 처리
                }) {
                    Text("갤러리에서 추가")
                }

                Button(onClick = {
                    showAddSheet = false
                    navController.navigate("manualInput") // 직접 추가 화면
                }) {
                    Text("직접 추가")
                }
            }
        }
    }
}

@Composable
fun MedicineItem(medicine: Medicine, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // 아이콘과 텍스트를 중앙 정렬하기 위해 Column의 horizontalAlignment 지정
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1) 약 모양 아이콘
            Icon(
                imageVector = Icons.Default.Medication,          // 머터리얼 아이콘 사용
                contentDescription = "약 아이콘",
                modifier = Modifier.size(40.dp)                  // 원하는 크기로 조절
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 2) 약 이름
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // (선택) 남은 개수 표시
            medicine.remainingPillCount?.let {
                Text(
                    text = "남은 개수: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            } ?: Text(
                text = "남은 개수: 무제한",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MedicineEditDialog(
    medicine: Medicine,
    onDismiss: () -> Unit,
    onUpdate: (Int?) -> Unit,
    onDelete: () -> Unit
) {
    var countText by remember { mutableStateOf(medicine.remainingPillCount?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${medicine.name} 수정") },
        text = {
            Column {
                OutlinedTextField(
                    value = countText,
                    onValueChange = { newValue -> countText = newValue.filter { it.isDigit() } },
                    label = { Text("남은 개수 (비워두면 무제한)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val newCount = countText.toIntOrNull()
                onUpdate(newCount)
            }) { Text("저장") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) { Text("삭제") }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onDismiss) { Text("취소") }
            }
        }
    )
}
