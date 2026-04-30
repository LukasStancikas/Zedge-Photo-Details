package com.lukasstancikas.zedge_photos_details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lukasstancikas.zedge_photos_details.ui.list.PhotoListScreen
import com.lukasstancikas.zedge_photos_details.ui.theme.ZedgePhotosDetailsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ZedgePhotosDetailsTheme {
                PhotoListScreen()
            }
        }
    }
}
