package com.lukasstancikas.zedge_photos_details.core.data.mapper

import com.lukasstancikas.zedge_photos_details.core.database.model.PhotoEntity
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo
import com.lukasstancikas.zedge_photos_details.core.network.model.PhotoDto

/**
 *  Everything here is pretty much the same, as it's a simple homework case
 * */

fun PhotoEntity.toDomain(): Photo = Photo(
    id = id,
    author = author,
    width = width,
    height = height,
    url = url,
    downloadUrl = downloadUrl,
    isFavorite = isFavorite
)

fun Photo.toEntity(): PhotoEntity = PhotoEntity(
    id = id,
    author = author,
    width = width,
    height = height,
    url = url,
    downloadUrl = downloadUrl,
    isFavorite = isFavorite
)

fun PhotoDto.toDomain(): Photo = Photo(
    id = id,
    author = author,
    width = width,
    height = height,
    url = url,
    downloadUrl = downloadUrl,
    isFavorite = false
)