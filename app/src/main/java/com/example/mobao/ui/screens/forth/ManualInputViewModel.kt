package com.example.mobao.ui.screens.manual

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime

data class ManualMedicineInput(
    val name: String = "",
    val dosePerDay: Int = 1,
    val time: LocalTime = LocalTime.now(),
    val totalCount: Int? = null
)

class ManualInputViewModel : ViewModel() {

    private val _medicineInput = MutableStateFlow(ManualMedicineInput())
    val medicineInput: StateFlow<ManualMedicineInput> = _medicineInput

    fun updateName(name: String) {
        _medicineInput.value = _medicineInput.value.copy(name = name)
    }

    fun updateDose(dose: Int) {
        _medicineInput.value = _medicineInput.value.copy(dosePerDay = dose)
    }

    fun updateTime(time: LocalTime) {
        _medicineInput.value = _medicineInput.value.copy(time = time)
    }

    fun updateTotalCount(count: Int?) {
        _medicineInput.value = _medicineInput.value.copy(totalCount = count)
    }

    fun resetInput() {
        _medicineInput.value = ManualMedicineInput()
    }
}
