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
                // When saving to DB, we use upsert to preserve isFavorite status automatically
                val ids = dao.upsertPhotosPreservingFavorite(
                    networkResult.data.map { it.toEntity() }
                )

                // Fetch updated entities to get the correct isFavorite status
                val updatedEntities = dao.getPhotosByIds(ids)
                Loadable.Success(updatedEntities.map { it.toDomain() })
            } catch (e: Exception) {
                // don't fail if network succeeded but DB insertion failed
                e.printStackTrace()
                networkResult
            }
        } else {
            try {
                val cachedPhotos = dao.getPhotos()
                Loadable.Success(cachedPhotos.map { it.toDomain() })
            } catch (e: Exception) {
                Loadable.Error(e)
            }
        }
    }

    override suspend fun getFavoritePhotos(): Loadable<List<Photo>> {
        return try {
            val favorites = dao.getFavoritePhotos()
            Loadable.Success(favorites.map { it.toDomain() })
        } catch (e: Exception) {
            Loadable.Error(e)
        }
    }

    override suspend fun getPhoto(id: String): Loadable<Photo> {
        return try {
            val entity = dao.getPhoto(id)
            if (entity != null) {
                Loadable.Success(entity.toDomain())
            } else {
                Loadable.Error(Exception("Photo not found"))
            }
        } catch (e: Exception) {
            Loadable.Error(e)
        }
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
    }

    override suspend fun clearPhotos() {
        dao.clearPhotos()
    }
}
