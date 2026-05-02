package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lukasstancikas.zedge_photos_details.core.ui.FullScreenScaffold
import com.lukasstancikas.zedge_photos_details.core.ui.ReloadableContent
import com.lukasstancikas.zedge_photos_details.feature.photolist.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    onNavigateToDetails: (String) -> Unit,
    viewModel: PhotoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PhotoListEffect.NavigateToDetails -> onNavigateToDetails(effect.photoId)
            }
        }
    }

    FullScreenScaffold(
        title = stringResource(R.string.list_title),
        actions = {
            IconButton(onClick = { viewModel.action(PhotoListAction.ToggleFavoritesFilter) }) {
                Icon(
                    imageVector = if (uiState.showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.menu_favorites_filter)
                )
            }
            IconButton(onClick = { viewModel.action(PhotoListAction.ClearPhotos) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.menu_clear_photos)
                )
            }
        },
        content = { systemBars ->
            ReloadableContent(
                loadable = uiState.photos,
                onRetry = { viewModel.action(PhotoListAction.LoadPhotos) },
                onRefresh = { viewModel.action(PhotoListAction.Refresh) },
            ) { photos ->
                PhotoListContent(
                    photos = photos,
                    onPhotoClick = { viewModel.action(PhotoListAction.PhotoClicked(it)) },
                    contentPadding = PaddingValues(bottom = systemBars.calculateBottomPadding())
                )
            }
        }
    )
}
