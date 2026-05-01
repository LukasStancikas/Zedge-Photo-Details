package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo

data class PhotoListUiState(
    val photos: Loadable<List<Photo>> = Loadable.Loading,
    val isRefreshing: Boolean = false
)