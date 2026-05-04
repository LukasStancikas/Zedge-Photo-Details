package com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation

sealed interface PhotoDetailsEffect {
    data class SharePhotoUrl(val url: String) : PhotoDetailsEffect
}
