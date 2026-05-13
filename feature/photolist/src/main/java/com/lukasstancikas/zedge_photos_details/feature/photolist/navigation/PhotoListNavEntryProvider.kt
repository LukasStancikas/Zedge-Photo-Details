package com.lukasstancikas.zedge_photos_details.feature.photolist.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lukasstancikas.zedge_photos_details.core.navigation.FeatureNavEntryProvider
import com.lukasstancikas.zedge_photos_details.core.navigation.Navigator
import com.lukasstancikas.zedge_photos_details.core.navigation.PhotoDetailsDestination
import com.lukasstancikas.zedge_photos_details.core.navigation.PhotoListDestination
import com.lukasstancikas.zedge_photos_details.feature.photolist.presentation.PhotoListScreen
import javax.inject.Inject

class PhotoListNavEntryProvider @Inject constructor() : FeatureNavEntryProvider {

    override fun EntryProviderScope<NavKey>.registerEntries(navigator: Navigator) {
        entry<PhotoListDestination> {
            PhotoListScreen(
                onNavigateToDetails = { photoId ->
                    navigator.navigate(PhotoDetailsDestination(photoId))
                },
            )
        }
    }
}
