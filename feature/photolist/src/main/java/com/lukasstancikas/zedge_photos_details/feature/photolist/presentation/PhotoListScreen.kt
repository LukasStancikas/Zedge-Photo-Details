package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lukasstancikas.zedge_photos_details.core.common.showErrorToast
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PhotoListEffect.NavigateToDetails -> onNavigateToDetails(effect.photoId)
                is PhotoListEffect.ShowErrorToast -> showErrorToast(context, effect.error)
            }
        }
    }

    FullScreenScaffold(
        title = stringResource(
            if (!uiState.showFavoritesOnly) {
                R.string.list_title
            } else {
                R.string.list_title_favorites
            }
        ),
        actions = {
            if (!uiState.showFavoritesOnly) {
                IconButton(onClick = { viewModel.action(PhotoListAction.PullRefresh) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.menu_refresh_photos)
                    )
                }
            }
            IconButton(onClick = { viewModel.action(PhotoListAction.ToggleFavoritesFilter) }) {
                Icon(
                    imageVector = if (uiState.showFavoritesOnly) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = stringResource(R.string.menu_favorites_filter)
                )
            }
        },
        content = { systemBars ->
            ReloadableContent(
                loadable = uiState.photos,
                isEnabled = !uiState.showFavoritesOnly,
                onRetry = { viewModel.action(PhotoListAction.LoadPhotos) },
                onRefresh = { viewModel.action(PhotoListAction.PullRefresh) },
            ) { photos ->
                PhotoListContent(
                    photos = photos,
                    onPhotoClick = { viewModel.action(PhotoListAction.PhotoClicked(it)) },
                    onLoadNextPage = { viewModel.action(PhotoListAction.LoadNextPage) },
                    isNextPageLoading = uiState.isNextPageLoading,
                    contentPadding = PaddingValues(bottom = systemBars.calculateBottomPadding())
                )
            }
        }
    )
}
