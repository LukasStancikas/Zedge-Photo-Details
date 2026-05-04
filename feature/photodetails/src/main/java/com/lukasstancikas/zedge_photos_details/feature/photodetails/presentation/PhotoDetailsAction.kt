package com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation

sealed interface PhotoDetailsAction {
    data object LoadPhoto : PhotoDetailsAction
    data object ToggleFavorite : PhotoDetailsAction
    data object SharePhoto : PhotoDetailsAction
}
