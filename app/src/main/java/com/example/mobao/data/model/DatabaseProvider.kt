package com.example.mobao.data.model

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mobao_database"
            )
                .fallbackToDestructiveMigration() // ← DB 초기화 허용
                .build()
            INSTANCE = instance
            instance
        }
    }
}
