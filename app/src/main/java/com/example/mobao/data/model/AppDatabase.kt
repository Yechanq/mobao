package com.example.mobao.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Medicine::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // 2 → 3 버전 마이그레이션: 새로운 reminderTimes 컬럼 추가 및 기존 JSON 데이터 복사
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1) reminderTimes 컬럼 추가 (TEXT, NOT NULL, 기본값 '[]')
                db.execSQL("""
                    ALTER TABLE medicines
                    ADD COLUMN reminderTimes TEXT NOT NULL DEFAULT '[]'
                """.trimIndent())

                // 2) 기존 reminderTimesJson 컬럼의 데이터를 그대로 reminderTimes 로 복사
                db.execSQL("""
                    UPDATE medicines
                    SET reminderTimes = reminderTimesJson
                """.trimIndent())

                // (선택) 원본 JSON 컬럼을 제거하려면, 테이블 재생성 방식이 필요합니다.
            }
        }

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mobao.db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
    }
}


