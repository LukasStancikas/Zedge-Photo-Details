package com.lukasstancikas.zedge_photos_details.core.domain.repository

import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(page: Int, limit: Int): Loadable<List<Photo>>

    suspend fun getPhoto(id: String): Loadable<Photo>

    suspend fun toggleFavorite(id: String, isFavorite: Boolean)

    suspend fun clearPhotos()
}
