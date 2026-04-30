package com.lukasstancikas.zedge_photos_details.data.api

import com.lukasstancikas.zedge_photos_details.data.model.Photo
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {
    @GET("v2/list")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Photo>
}
