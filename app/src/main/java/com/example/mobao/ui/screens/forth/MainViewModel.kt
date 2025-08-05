package com.example.mobao.ui.screens.forth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobao.data.model.Medicine
import com.example.mobao.data.model.DatabaseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import kotlinx.coroutines.flow.Flow

class MainViewModel(context: Context) : ViewModel() {

    private val medicineDao = DatabaseProvider.getDatabase(context).medicineDao()

    val medicines: Flow<List<Medicine>> = medicineDao.getAllMedicinesFlow()

    fun updateMedicineCount(medicine: Medicine, newCount: Int?) {
        viewModelScope.launch {
            val updated = medicine.copy(remainingPillCount = newCount)
            medicineDao.updateMedicine(updated)
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            medicineDao.deleteMedicine(medicine)
        }
    }
}
