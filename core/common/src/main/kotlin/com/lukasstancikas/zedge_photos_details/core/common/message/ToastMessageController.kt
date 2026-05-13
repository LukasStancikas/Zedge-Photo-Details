package com.lukasstancikas.zedge_photos_details.core.common.message

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToastMessageController @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : MessageController {
    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

