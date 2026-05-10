package com.abhiumale.kartikmartapp.di

import android.app.Application
import androidx.room.Room
import com.abhiumale.kartikmartapp.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "kartikmart_db"
        ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideCartDao(db: AppDatabase) = db.cartDao()
}