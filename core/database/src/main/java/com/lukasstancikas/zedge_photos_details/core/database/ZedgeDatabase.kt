package com.lukasstancikas.zedge_photos_details.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lukasstancikas.zedge_photos_details.core.database.dao.PhotoDao
import com.lukasstancikas.zedge_photos_details.core.database.model.PhotoEntity

@Database(
    entities = [PhotoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ZedgeDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}
