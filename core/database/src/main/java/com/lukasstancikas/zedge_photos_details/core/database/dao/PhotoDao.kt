package com.lukasstancikas.zedge_photos_details.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lukasstancikas.zedge_photos_details.core.database.model.PhotoEntity

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    suspend fun getPhotos(): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Query("DELETE FROM photos")
    suspend fun clearPhotos()
}
