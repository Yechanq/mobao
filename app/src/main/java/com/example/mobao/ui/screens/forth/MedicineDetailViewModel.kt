package com.example.mobao.ui.screens.forth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobao.data.model.Medicine
import com.example.mobao.data.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor(
    private val repository: MedicineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // 1) 경로 파라미터에서 medicineId 추출
    private val medId: Int = checkNotNull(savedStateHandle["medicineId"])

    // 2) Flow로 가져오기
    val medicine: StateFlow<Medicine?> = repository
        .getMedicineByIdFlow(medId)  // 아래에 getMedicineByIdFlow 가정
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, null)

    fun deleteMedicine() = viewModelScope.launch {
        medicine.value?.let {
            repository.deleteMedicine(it)
        }
    }

    fun saveMedicineDetails(newCount: Int?, newTimes: List<LocalTime>) = viewModelScope.launch {
        medicine.value?.let { med ->
            repository.updateMedicineCountAndTimes(med, newCount, newTimes)
        }
    }
}
