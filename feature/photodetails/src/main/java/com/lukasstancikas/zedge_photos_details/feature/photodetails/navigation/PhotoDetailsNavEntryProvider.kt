package com.lukasstancikas.zedge_photos_details.feature.photodetails.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lukasstancikas.zedge_photos_details.core.navigation.FeatureNavEntryProvider
import com.lukasstancikas.zedge_photos_details.core.navigation.Navigator
import com.lukasstancikas.zedge_photos_details.core.navigation.PhotoDetailsDestination
import com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation.PhotoDetailsScreen
import javax.inject.Inject

class PhotoDetailsNavEntryProvider @Inject constructor() : FeatureNavEntryProvider {

    override fun EntryProviderScope<NavKey>.registerEntries(navigator: Navigator) {
        entry<PhotoDetailsDestination> { destination ->
            PhotoDetailsScreen(
                photoId = destination.photoId,
                onBackClick = { navigator.goBack() },
            )
        }
    }
}
