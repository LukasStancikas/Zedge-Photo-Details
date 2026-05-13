package com.lukasstancikas.zedge_photos_details

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lukasstancikas.zedge_photos_details.feature.photodetails.navigation.PhotoDetailsDestination
import com.lukasstancikas.zedge_photos_details.feature.photolist.navigation.PhotoListDestination
import com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation.PhotoDetailsScreen
import com.lukasstancikas.zedge_photos_details.feature.photolist.presentation.PhotoListScreen

@Composable
fun ZedgeNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = PhotoListDestination,
    ) {
        composable<PhotoListDestination> {
            PhotoListScreen(
                onNavigateToDetails = { photoId ->
                    navController.navigate(PhotoDetailsDestination(photoId))
                },
            )
        }
        composable<PhotoDetailsDestination> {
            PhotoDetailsScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
