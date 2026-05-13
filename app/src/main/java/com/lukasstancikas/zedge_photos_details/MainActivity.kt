package com.lukasstancikas.zedge_photos_details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lukasstancikas.zedge_photos_details.core.navigation.FeatureNavEntryProvider
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navEntryProviders: Set<@JvmSuppressWildcards FeatureNavEntryProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ZedgePhotosDetailsTheme {
                ZedgeNavDisplay(navigationProviders = navEntryProviders)
            }
        }
    }
}
