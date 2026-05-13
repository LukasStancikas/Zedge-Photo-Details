package com.lukasstancikas.zedge_photos_details.core.common.sharing

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ContentSharingControllerModule {
    @Binds
    abstract fun bindContentSharingController(impl: PhotoUrlSharingController): ContentSharingController
}