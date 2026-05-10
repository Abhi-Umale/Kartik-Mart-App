package com.abhiumale.kartikmartapp.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "kartik_mart_db"
            ).fallbackToDestructiveMigration(true)
                .build()

            INSTANCE = instance

            instance
        }
    }
}