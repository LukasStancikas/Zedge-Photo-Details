package com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.common.sharePhotoUrl
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo
import com.lukasstancikas.zedge_photos_details.core.ui.FullScreenScaffold
import com.lukasstancikas.zedge_photos_details.core.ui.LoadableContent
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme
import com.lukasstancikas.zedge_photos_details.feature.photodetails.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoDetailsViewModel = hiltViewModel()
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
        topAppBar = {
            val photo = (uiState.photo as? Loadable.Success)?.data
            DetailsTopAppBar(photo, onBackClick, viewModel::action)
        },
        content = { paddingValues ->
            LoadableContent(
                loadable = uiState.photo,
                onRetry = { viewModel.action(PhotoDetailsAction.LoadPhoto) },
                modifier = modifier.padding(paddingValues)
            ) { photo ->
                PhotoDetailsContent(
                    photo = photo,
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopAppBar(photo: Photo?, onBackClick: () -> Unit, action: (PhotoDetailsAction) -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.title_photo_details)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.menu_back)
                )
            }
        },
        actions = {
            if (photo != null) {
                val isFavorite = photo.isFavorite
                IconButton(onClick = { action(PhotoDetailsAction.SharePhoto) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.menu_share)
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
                                R.string.menu_favorites_add
                            )
                        },
                        tint = if (isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PhotoDetailsScreenPreview() {
    val samplePhoto = Photo(
        id = "0",
        author = "Alejandro Escamilla",
        width = 5000,
        height = 3333,
        url = "https://unsplash.com/photos/LNR_chXmC64",
        downloadUrl = "https://picsum.photos/id/0/5000/3333"
    )

    ZedgePhotosDetailsTheme {
        FullScreenScaffold(
            topAppBar = {
                DetailsTopAppBar(
                    photo = samplePhoto,
                    onBackClick = {},
                    action = {}
                )
            },
            content = { paddingValues ->
                PhotoDetailsContent(
                    photo = samplePhoto,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        )
    }
}
