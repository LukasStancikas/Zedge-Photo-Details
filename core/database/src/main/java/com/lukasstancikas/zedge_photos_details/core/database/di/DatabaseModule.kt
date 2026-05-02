package com.lukasstancikas.zedge_photos_details.core.database.di

import android.content.Context
import androidx.room.Room
import com.lukasstancikas.zedge_photos_details.core.database.ZedgeDatabase
import com.lukasstancikas.zedge_photos_details.core.database.dao.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideZedgeDatabase(
        @ApplicationContext context: Context,
    ): ZedgeDatabase = Room.databaseBuilder(
        context,
        ZedgeDatabase::class.java,
        "zedge-database",
    ).fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun providePhotoDao(
        database: ZedgeDatabase,
    ): PhotoDao = database.photoDao()
}
