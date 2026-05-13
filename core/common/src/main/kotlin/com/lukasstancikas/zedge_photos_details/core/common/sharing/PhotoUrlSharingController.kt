package com.lukasstancikas.zedge_photos_details.core.common.sharing

import android.content.Context
import android.content.Intent
import com.lukasstancikas.zedge_photos_details.core.common.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoUrlSharingController @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ContentSharingController {
    override fun sharePhotoUrl(url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.share_photo),
            ),
        )
    }
}

