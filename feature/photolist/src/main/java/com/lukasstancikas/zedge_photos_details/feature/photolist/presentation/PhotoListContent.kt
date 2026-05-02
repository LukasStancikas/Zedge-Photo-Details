package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme
import com.lukasstancikas.zedge_photos_details.feature.photolist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListContent(
    photos: List<Photo>,
    onPhotoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(photos) { photo ->
            PhotoItem(
                photo = photo,
                onClick = { onPhotoClick(photo.id) }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PhotoItem(
    photo: Photo,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            GlideImage(
                model = photo.downloadUrl,
                contentDescription = stringResource(
                    R.string.photo_content_description,
                    photo.author
                ),
                loading = placeholder {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Text(
                    text = photo.author,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.photo_fake_description,
                        photo.author,
                        photo.width,
                        photo.height
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoListContentPreview() {
    ZedgePhotosDetailsTheme {
        PhotoListContent(
            photos = listOf(
                Photo(
                    id = "1",
                    author = "Alejandro Escamilla",
                    width = 5000,
                    height = 3333,
                    url = "https://unsplash.com/photos/LNR_chXmC6c",
                    downloadUrl = "https://picsum.photos/id/0/5000/3333"
                ),
                Photo(
                    id = "2",
                    author = "Paul Jarvis",
                    width = 2500,
                    height = 1667,
                    url = "https://unsplash.com/photos/6J--NX9BIuM",
                    downloadUrl = "https://picsum.photos/id/10/2500/1667"
                )
            ),
            onPhotoClick = {}
        )
    }
}