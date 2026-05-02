package com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme
import com.lukasstancikas.zedge_photos_details.feature.photodetails.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PhotoDetailsContent(
    photo: Photo,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(start = 16.dp, end = 16.dp)
    ) {
        GlideImage(
            model = photo.downloadUrl,
            loading = placeholder {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            },
            contentDescription = stringResource(R.string.photo_details_photo_by, photo.author),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(photo.width.toFloat() / photo.height.toFloat()),
            contentScale = ContentScale.Fit
        )

        Text(
            text = stringResource(R.string.photo_details_author, photo.author),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(R.string.photo_details_dimensions, photo.width, photo.height),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = stringResource(R.string.photo_details_original_url, photo.url),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoDetailsContentPreview() {
    ZedgePhotosDetailsTheme {
        PhotoDetailsContent(
            photo = Photo(
                id = "1",
                author = "Alejandro Escamilla",
                width = 5000,
                height = 3333,
                url = "https://unsplash.com/photos/LNR_chXmC6c",
                downloadUrl = "https://picsum.photos/id/0/5000/3333"
            ),
        )
    }
}