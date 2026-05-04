package com.lukasstancikas.zedge_photos_details.core.database.model

data class PhotoMetadata(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val downloadUrl: String,
)
