package com.example.mobao.data.repository

import android.content.Context
import com.example.mobao.data.model.DatabaseProvider
import com.example.mobao.data.model.Medicine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import javax.inject.Inject
import com.example.mobao.util.AlarmHelper

class MedicineRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dao = DatabaseProvider.getDatabase(context).medicineDao()

    /** 전체 약 목록 Flow */
    fun getAllMedicinesFlow(): Flow<List<Medicine>> =
        dao.getAllMedicinesFlow()

    /** 단일 약 조회 Flow */
    fun getMedicineByIdFlow(medicineId: Int): Flow<Medicine> =
        dao.getMedicineByIdFlow(medicineId)

    /**
     * 신규 약 삽입 + 복용 시간 알람 등록
     * - count가 null 또는 0 이하인 경우 알람을 등록하지 않습니다.
     */
    suspend fun insertMedicineWithTimes(
        name: String,
        times: List<LocalTime>,
        count: Int?
    ) {
        val medicine = Medicine(
            name = name,
            reminderTimes = times,
            totalPillCount = count,
            remainingPillCount = count
        )

        // 1) DB 삽입
        val id = dao.insertMedicine(medicine).toInt()

        // 2) 알람 등록 (남은 개수 > 0일 때만)
        if ((count ?: 0) > 0) {
            AlarmHelper.scheduleReminders(context, id, times)
        }
    }

    /** 일반 업데이트 (필드 전체 교체) */
    suspend fun updateMedicine(medicine: Medicine) {
        dao.updateMedicine(medicine)
    }

    /**
     * 삭제 시 알람도 함께 취소
     */
    suspend fun deleteMedicine(medicine: Medicine) {
        AlarmHelper.cancelReminders(context, medicine.id, medicine.reminderTimes)
        dao.deleteMedicine(medicine)
    }

    suspend fun updateMedicineWithTimes(
        medicine: Medicine,
        newTimes: List<LocalTime>
    ) {
        // 1) 이전 알람 취소
        AlarmHelper.cancelReminders(context, medicine.id, medicine.reminderTimes)

        // 2) DB 업데이트
        val updated = medicine.copy(reminderTimes = newTimes)
        dao.updateMedicine(updated)

        // 3) 새 알람 등록
        val remaining = updated.remainingPillCount ?: updated.totalPillCount ?: 0
        if (remaining > 0) {
            AlarmHelper.scheduleReminders(context, updated.id, newTimes)
        }
    }

    suspend fun updateRemainingCount(
        medicine: Medicine,
        newCount: Int?
    ) {
        val updated = medicine.copy(remainingPillCount = newCount)
        dao.updateMedicine(updated)

        if ((newCount ?: 0) <= 0) {
            AlarmHelper.cancelReminders(context, updated.id, updated.reminderTimes)
        }
    }

    /** 지정한 약의 모든 알람을 즉시 취소 */
    suspend fun cancelAlarmsForMedicine(medicine: Medicine) {
        AlarmHelper.cancelReminders(context, medicine.id, medicine.reminderTimes)
    }
    suspend fun updateMedicineCountAndTimes(
        medicine: Medicine,
        newCount: Int?,
        newTimes: List<LocalTime>
    ) {
        // 1) 기존 알람 모두 취소
        AlarmHelper.cancelReminders(context, medicine.id, medicine.reminderTimes)

        // 2) DB에 두 값을 한꺼번에 반영
        val updated = medicine.copy(
            remainingPillCount = newCount,
            reminderTimes = newTimes
        )
        dao.updateMedicine(updated)

        // 3) 남은 개수가 1 이상일 때만 새 알람 등록
        if ((newCount ?: 0) > 0) {
            AlarmHelper.scheduleReminders(context, medicine.id, newTimes)
        }
    }
}
