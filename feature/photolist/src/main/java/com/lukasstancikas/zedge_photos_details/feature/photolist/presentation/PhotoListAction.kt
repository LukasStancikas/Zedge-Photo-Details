package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

sealed interface PhotoListAction {
    data object LoadPhotos : PhotoListAction
    data object LoadNextPage : PhotoListAction
    data object PullRefresh : PhotoListAction
    data object ToggleFavoritesFilter : PhotoListAction
    data class PhotoClicked(val photoId: String) : PhotoListAction
}