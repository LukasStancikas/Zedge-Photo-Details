package com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.common.sharePhotoUrl
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo
import com.lukasstancikas.zedge_photos_details.core.ui.FullScreenScaffold
import com.lukasstancikas.zedge_photos_details.core.ui.LoadableContent
import com.lukasstancikas.zedge_photos_details.feature.photodetails.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PhotoDetailsEffect.SharePhotoUrl -> sharePhotoUrl(effect.url, context)
            }
        }
    }

    FullScreenScaffold(
        title = stringResource(R.string.title_photo_details),
        onBackClick = onBackClick,
        actions = {
            val photo = (uiState.photo as? Loadable.Success)?.data
            DetailsAppBarActions(photo, viewModel::action)
        },
        content = { systemBars ->
            LoadableContent(
                loadable = uiState.photo,
                onRetry = { viewModel.action(PhotoDetailsAction.LoadPhoto) },
                content = { photo ->
                    PhotoDetailsContent(
                        photo = photo,
                        contentPadding = PaddingValues(bottom = systemBars.calculateBottomPadding()),
                    )
                },
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsAppBarActions(photo: Photo?, action: (PhotoDetailsAction) -> Unit) {
    if (photo != null) {
        val isFavorite = photo.isFavorite
        IconButton(onClick = { action(PhotoDetailsAction.SharePhoto) }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.menu_share),
            )
        }
        IconButton(onClick = { action(PhotoDetailsAction.ToggleFavorite) }) {
            Icon(
                imageVector = if (isFavorite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = if (isFavorite) {
                    stringResource(R.string.menu_favorites_remove)
                } else {
                    stringResource(
                        R.string.menu_favorites_add,
                    )
                },
            )
        }
    }
}
