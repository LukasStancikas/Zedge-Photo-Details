package com.lukasstancikas.zedge_photos_details.feature.photolist.navigation

import com.lukasstancikas.zedge_photos_details.core.navigation.FeatureNavEntryProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class PhotoListNavModule {

    @Binds
    @IntoSet
    abstract fun bindPhotoListNavEntryProvider(
        impl: PhotoListNavEntryProvider,
    ): FeatureNavEntryProvider
}
