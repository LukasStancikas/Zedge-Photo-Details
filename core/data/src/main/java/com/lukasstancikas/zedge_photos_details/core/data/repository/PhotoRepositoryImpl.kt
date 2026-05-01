package com.lukasstancikas.zedge_photos_details.core.data.repository

import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable.Companion.mapAll
import com.lukasstancikas.zedge_photos_details.core.data.mapper.toDomain
import com.lukasstancikas.zedge_photos_details.core.data.mapper.toEntity
import com.lukasstancikas.zedge_photos_details.core.database.dao.PhotoDao
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo
import com.lukasstancikas.zedge_photos_details.core.domain.repository.PhotoRepository
import com.lukasstancikas.zedge_photos_details.core.network.PicsumApi
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val api: PicsumApi,
    private val dao: PhotoDao
) : PhotoRepository {

    override suspend fun getPhotos(page: Int, limit: Int): Loadable<List<Photo>> {
        val networkResult = api.getPhotos(page, limit).mapAll { it.toDomain() }

        return if (networkResult is Loadable.Success) {
            try {
                dao.insertPhotos(networkResult.data.map { it.toEntity() })
            } catch (e: Exception) {
                // don't fail if network succeeded but DB insertion failed
                e.printStackTrace()
            }
            networkResult
        } else {
            try {
                val cachedPhotos = dao.getPhotos()
                Loadable.Success(cachedPhotos.map { it.toDomain() })
            } catch (e: Exception) {
                Loadable.Error(e)
            }
        }
    }

    override suspend fun clearPhotos() {
        dao.clearPhotos()
    }
}
