package com.lukasstancikas.zedge_photos_details.data.repository

import com.lukasstancikas.zedge_photos_details.data.api.PicsumApi
import com.lukasstancikas.zedge_photos_details.data.model.Photo
import com.lukasstancikas.zedge_photos_details.ui.Loadable
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val api: PicsumApi
) : PhotoRepository {
    override suspend fun getPhotos(page: Int, limit: Int): Loadable<List<Photo>> {
        return try {
            val response = api.getPhotos(page, limit)
            Loadable.Success(response)
        } catch (e: Exception) {
            Loadable.Error(e)
        }
    }
}
