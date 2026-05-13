package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo

data class PhotoListUiState(
    val loadedState: Loadable<LoadedState> = Loadable.Loading,
    val showFavoritesOnly: Boolean = false,
    val isNextPageLoading: Boolean = false,
) {
    data class LoadedState(
        val photos: List<Photo>
    )
}
