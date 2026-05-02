package com.lukasstancikas.zedge_photos_details.core.common

import android.content.Context
import android.content.Intent

fun sharePhotoUrl(
    url: String,
    context: Context
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.share_photo)
        )
    )
}