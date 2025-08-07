package com.example.mobao.ui.screens.forth

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import java.time.LocalTime
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(navController: NavController) {
    val viewModel: AddMedicineViewModel = hiltViewModel()
    val context = LocalContext.current
    val prescriptionInfo by viewModel.prescriptionInfo.collectAsStateWithLifecycle()

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var name by remember { mutableStateOf("") }
    var showAddSheet by remember { mutableStateOf(false) }

    // 카메라 촬영 런처
    val cameraLauncher = rememberLauncherForActivityResult(TakePicturePreview()) { bitmap ->
        bitmap?.let {
            selectedBitmap = it
            viewModel.analyzePrescriptionImage(it)
        }
    }
    // 갤러리 사진 선택 런처
    val galleryLauncher = rememberLauncherForActivityResult(PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= 28) {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
            selectedBitmap = bitmap
            viewModel.analyzePrescriptionImage(bitmap)
        }
    }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            selectedBitmap?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = "선택한 이미지")
                Spacer(modifier = Modifier.height(16.dp))
            }

            prescriptionInfo?.let { info ->
                Text("하루 복용 횟수: ${info.dailyDoseCount ?: 0}", fontSize = 18.sp)
                Text("총 복용량: ${info.totalPillCount ?: 0}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("약 이름") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (name.isNotBlank()) {
                        // 1) DB에 삽입 + 알림 스케줄
                        viewModel.addManualMedicineWithTimes(
                            name = name,
                            times = listOf(LocalTime.now()),
                            count = info.totalPillCount
                        )
                        // 2) MainScreen으로 이동
                        navController.navigate("forthMain") {
                            popUpTo("first") { inclusive = false }
                        }
                    }
                }) {
                    Text("OCR 정보로 약 등록")
                }
            }
        }
    }

    // BottomSheet: 추가 방법 선택
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
                    cameraLauncher.launch(null)
                }) {
                    Text("카메라로 추가")
                }

                Button(onClick = {
                    showAddSheet = false
                    galleryLauncher.launch(PickVisualMediaRequest(ImageOnly))
                }) {
                    Text("갤러리에서 추가")
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
