package com.lukasstancikas.zedge_photos_details.core.data.di

import com.lukasstancikas.zedge_photos_details.core.data.repository.PhotoRepositoryImpl
import com.lukasstancikas.zedge_photos_details.core.domain.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): PhotoRepository
}
