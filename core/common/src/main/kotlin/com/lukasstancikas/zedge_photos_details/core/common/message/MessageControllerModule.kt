package com.lukasstancikas.zedge_photos_details.core.common.message

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageControllerModule {
    @Binds
    abstract fun bindMessageController(impl: ToastMessageController): MessageController
}