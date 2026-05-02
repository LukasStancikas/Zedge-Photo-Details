package com.lukasstancikas.zedge_photos_details.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lukasstancikas.zedge_photos_details.core.database.model.PhotoEntity
import com.lukasstancikas.zedge_photos_details.core.database.model.PhotoMetadata

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    suspend fun getPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE isFavorite = 1")
    suspend fun getFavoritePhotos(): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotosIgnore(photos: List<PhotoEntity>)

    // partial updates with parameters
    @Update(entity = PhotoEntity::class)
    suspend fun updatePhotosMetadata(photos: List<PhotoMetadata>)

    // Use this for insertion, inserts missing photos, updates all photos data, except isFavorite
    @Transaction
    suspend fun upsertPhotosPreservingFavorite(photos: List<PhotoEntity>): List<String> {
        insertPhotosIgnore(photos)
        updatePhotosMetadata(photos.map {
            PhotoMetadata(it.id, it.author, it.width, it.height, it.url, it.downloadUrl)
        })
        return photos.map { it.id }
    }

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhoto(id: String): PhotoEntity?

    @Query("SELECT * FROM photos WHERE id IN (:ids)")
    suspend fun getPhotosByIds(ids: List<String>): List<PhotoEntity>

    @Query("UPDATE photos SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("DELETE FROM photos")
    suspend fun clearPhotos()
}

