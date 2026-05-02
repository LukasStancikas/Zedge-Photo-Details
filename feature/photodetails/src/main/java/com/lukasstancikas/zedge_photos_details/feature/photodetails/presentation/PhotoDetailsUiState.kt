package com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation

import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo

data class PhotoDetailsUiState(
    val photoId: String,
    val photo: Loadable<Photo> = Loadable.Loading,
)