package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo
import com.lukasstancikas.zedge_photos_details.core.ui.FullScreenScaffold
import com.lukasstancikas.zedge_photos_details.core.ui.ReloadableContent
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme
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
            IconButton(onClick = { viewModel.action(PhotoListAction.ClearPhotos) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.menu_clear_photos)
                )
            }
        },
        content = { paddingValues ->
            ReloadableContent(
                loadable = uiState.photos,
                onRetry = { viewModel.action(PhotoListAction.LoadPhotos) },
                onRefresh = { viewModel.action(PhotoListAction.Refresh) },
                modifier = Modifier
                    .padding(paddingValues)
            ) { photos ->
                PhotoListContent(
                    photos = photos,
                    onPhotoClick = { viewModel.action(PhotoListAction.PhotoClicked(it)) },
                )
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true,)
@Composable
private fun PhotoListScreenPreview() {
    val samplePhotos = listOf(
        Photo(
            id = "0",
            author = "Alejandro Escamilla",
            width = 5000,
            height = 3333,
            url = "https://unsplash.com/photos/LNR_chXmC64",
            downloadUrl = "https://picsum.photos/id/0/5000/3333"
        ),
        Photo(
            id = "1",
            author = "Alejandro Escamilla",
            width = 5000,
            height = 3333,
            url = "https://unsplash.com/photos/LNR_chXmC64",
            downloadUrl = "https://picsum.photos/id/1/5000/3333"
        )
    )

    ZedgePhotosDetailsTheme {
        FullScreenScaffold(
            title = stringResource(R.string.list_title),
            content = { paddingValues ->
                PhotoListContent(
                    photos = samplePhotos,
                    onPhotoClick = {},
                    modifier = Modifier.padding(paddingValues)
                )
            }
        )
    }
}

