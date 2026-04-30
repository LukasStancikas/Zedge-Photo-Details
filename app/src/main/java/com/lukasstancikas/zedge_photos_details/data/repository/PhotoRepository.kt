package com.lukasstancikas.zedge_photos_details.data.repository

import com.lukasstancikas.zedge_photos_details.data.model.Photo
import com.lukasstancikas.zedge_photos_details.ui.Loadable

interface PhotoRepository {
    suspend fun getPhotos(page: Int, limit: Int): Loadable<List<Photo>>
}
