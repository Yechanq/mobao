package com.example.mobao.di

import android.content.Context
import com.example.mobao.data.repository.MedicineRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMedicineRepository(
        @ApplicationContext context: Context
    ): MedicineRepository {
        return MedicineRepository(context)
    }
}
