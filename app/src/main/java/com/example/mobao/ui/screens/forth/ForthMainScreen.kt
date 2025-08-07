package com.example.mobao.ui.screens.forth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mobao.data.model.Medicine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForthMainScreen(
    navController: NavController,
    viewModel: ForthMainViewModel
) {
    val allMedicines by viewModel.medicines.collectAsStateWithLifecycle(initialValue = emptyList())
    val activeMedicines = allMedicines.filter { it.remainingPillCount == null || it.remainingPillCount > 0 }
    val completedMedicines = allMedicines.filter { it.remainingPillCount == 0 }

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
            // 복용 중인 약 리스트
            Text(
                text = "복용 중인 약 (${activeMedicines.size})",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            if (activeMedicines.isEmpty()) {
                Text("복용 중인 약이 없습니다.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(activeMedicines) { medicine ->
                        MedicineItem(medicine) {
                            navController.navigate("medicineDetail/${medicine.id}")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 복용 완료 약 토글
            Text(
                text = "복용 완료한 약 (${completedMedicines.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable { completedVisible = !completedVisible }
            )
            Spacer(Modifier.height(8.dp))
            AnimatedVisibility(visible = completedVisible) {
                if (completedMedicines.isEmpty()) {
                    Text("복용 완료한 약이 없습니다.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(completedMedicines) { medicine ->
                            MedicineItem(medicine) {
                                navController.navigate("medicineDetail/${medicine.id}")
                            }
                        }
                    }
                }
            }
        }
    }

    // 약 추가 메서드 선택 BottomSheet
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
                    navController.navigate("addMedicine")
                }) {
                    Text("카메라/OCR로 추가")
                }

                Button(onClick = {
                    showAddSheet = false
                    navController.navigate("addMedicine")
                }) {
                    Text("갤러리로 추가")
                }

                Button(onClick = {
                    showAddSheet = false
                    navController.navigate("manualInput")
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
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Medication,
                contentDescription = "약 아이콘",
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = medicine.remainingPillCount?.let { "남은 개수: $it" } ?: "남은 개수: 무제한",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
