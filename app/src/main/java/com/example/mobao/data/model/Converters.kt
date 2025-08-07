package com.example.mobao.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalTime

object Converters {
    private val gson = Gson()

    // 저장: LocalTime 리스트 → 쉼표 구분 문자열
    @TypeConverter
    fun fromLocalTimeList(times: List<LocalTime>?): String =
        times?.joinToString(",") { it.toString() } ?: ""

    // 복원: 기존 JSON 포맷 혹은 쉼표 포맷 모두 처리
    @TypeConverter
    fun toLocalTimeList(value: String?): List<LocalTime> {
        if (value.isNullOrBlank()) return emptyList()

        return try {
            val trimmed = value.trim()
            if (trimmed.startsWith("[") && trimmed.contains("{")) {
                // 1) 기존 Gson JSON 포맷: List<LocalTime> 직렬화 결과
                //    실제로는 LocalTime 객체 JSON이 여의치 않으니,
                //    Gson이 문자열 리스트로 저장했다면 이 방식으로 캐스팅
                val type = object : TypeToken<List<String>>() {}.type
                val list: List<String> = gson.fromJson(trimmed, type)
                list.map { LocalTime.parse(it) }
            } else {
                // 2) 새로 적용한 쉼표 구분 HH:mm 포맷
                trimmed.split(",").map { LocalTime.parse(it) }
            }
        } catch (e: Exception) {
            // 어떤 이유로든 파싱 실패 시 빈 리스트로 안전 복귀
            emptyList()
        }
    }
}
