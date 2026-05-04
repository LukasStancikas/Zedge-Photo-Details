package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

sealed interface PhotoListEffect {
    data class NavigateToDetails(val photoId: String) : PhotoListEffect
    data class ShowErrorToast(val error: String) : PhotoListEffect
}
