package com.lukasstancikas.zedge_photos_details.feature.photodetails.navigation

import com.lukasstancikas.zedge_photos_details.core.navigation.FeatureNavEntryProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class PhotoDetailsNavModule {

    @Binds
    @IntoSet
    abstract fun bindPhotoDetailsNavEntryProvider(
        impl: PhotoDetailsNavEntryProvider,
    ): FeatureNavEntryProvider
}
