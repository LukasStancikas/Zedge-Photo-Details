package com.lukasstancikas.zedge_photos_details.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme

@Composable
fun <T> LoadableContent(
    loadable: Loadable<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    when (loadable) {
        is Loadable.Loading -> {
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is Loadable.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = loadable.throwable.message
                        ?: stringResource(R.string.error_message_unknown),
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }

        is Loadable.Success -> {
            content(loadable.data)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadableContentLoadingPreview() {
    ZedgePhotosDetailsTheme {
        LoadableContent(
            loadable = Loadable.Loading,
            onRetry = {},
            content = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadableContentErrorPreview() {
    ZedgePhotosDetailsTheme {
        LoadableContent(
            loadable = Loadable.Error(
                Throwable(stringResource(R.string.generic_error_something_went_wrong))
            ),
            onRetry = {},
            content = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadableContentSuccessPreview() {
    ZedgePhotosDetailsTheme {
        LoadableContent(
            loadable = Loadable.Success("Success Data"),
            onRetry = {}
        ) { data ->
            Text("Content: $data", modifier = Modifier.fillMaxSize())
        }
    }
}
