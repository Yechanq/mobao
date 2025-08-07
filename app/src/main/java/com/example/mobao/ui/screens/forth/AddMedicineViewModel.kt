package com.example.mobao.ui.screens.forth

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.mobao.data.repository.MedicineRepository
import com.example.mobao.data.model.PrescriptionInfo
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

@HiltViewModel
class AddMedicineViewModel @Inject constructor(
    private val repository: MedicineRepository
) : ViewModel() {

    private val _prescriptionInfo = MutableStateFlow<PrescriptionInfo?>(null)
    val prescriptionInfo: StateFlow<PrescriptionInfo?> = _prescriptionInfo

    /**
     * OCR 이미지 분석 (처방전 등)
     */
    fun analyzePrescriptionImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                val dailyDose = extractDailyDose(text)
                val totalPill = extractTotalPill(text)

                _prescriptionInfo.value = PrescriptionInfo(
                    dailyDoseCount = dailyDose,
                    totalPillCount = totalPill
                )
            }
            .addOnFailureListener {
                _prescriptionInfo.value = null
            }
    }

    private fun extractDailyDose(text: String): Int {
        val doseRegex = Regex("""[하1]루\s?(\d+)""")
        return doseRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }

    private fun extractTotalPill(text: String): Int {
        val pillRegex = Regex("""(\d+)[정알]""")
        return pillRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }

    /**
     * 개별 알림 시간 리스트 기반 약 추가
     */
    fun addManualMedicineWithTimes(name: String, times: List<LocalTime>, count: Int?) {
        viewModelScope.launch {
            repository.insertMedicineWithTimes(name, times, count)
        }
    }
}
