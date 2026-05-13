package com.lukasstancikas.zedge_photos_details.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDetailsDestination(val photoId: String = "") : NavKey
