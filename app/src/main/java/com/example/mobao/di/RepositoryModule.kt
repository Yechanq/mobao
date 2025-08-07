package com.example.mobao.di

import android.content.Context
import com.example.mobao.data.repository.MedicineRepository
import com.example.mobao.data.repository.PostRepository
import com.example.mobao.data.repository.PostRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    companion object {
        @Provides
        @Singleton
        fun provideMedicineRepository(
            @ApplicationContext context: Context
        ): MedicineRepository {
            return MedicineRepository(context)
        }
    }
}