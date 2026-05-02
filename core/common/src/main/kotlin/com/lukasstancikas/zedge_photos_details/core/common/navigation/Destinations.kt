package com.lukasstancikas.zedge_photos_details.core.common.navigation

import kotlinx.serialization.Serializable

@Serializable
object PhotoListDestination

@Serializable
data class PhotoDetailsDestination(val photoId: String)
