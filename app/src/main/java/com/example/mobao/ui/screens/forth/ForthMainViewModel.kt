package com.example.mobao.ui.screens.forth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobao.data.model.Medicine
import com.example.mobao.data.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalTime

@HiltViewModel
class ForthMainViewModel @Inject constructor(
    private val repository: MedicineRepository
) : ViewModel() {


    // Flow<List<Medicine>>를 StateFlow로 변환 → UI 자동 업데이트
    val medicines: StateFlow<List<Medicine>> = repository.getAllMedicinesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insertMedicineWithTimes(name: String, times: List<LocalTime>, count: Int?) {
        viewModelScope.launch {
            repository.insertMedicineWithTimes(name, times, count)
        }
    }

    fun updateMedicineCount(medicine: Medicine, newCount: Int?) {
        viewModelScope.launch {
            repository.updateMedicine(medicine.copy(remainingPillCount = newCount))
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.deleteMedicine(medicine)
        }
    }
}
