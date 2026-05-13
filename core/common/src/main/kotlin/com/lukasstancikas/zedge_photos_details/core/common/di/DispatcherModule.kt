package com.lukasstancikas.zedge_photos_details.core.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    /**
    * Declared @Qualifier annotated dispatchers for Main and Default if needed
    * */
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

}
