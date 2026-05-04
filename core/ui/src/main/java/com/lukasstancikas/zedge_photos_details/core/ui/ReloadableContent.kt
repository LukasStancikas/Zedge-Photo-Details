package com.lukasstancikas.zedge_photos_details.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ReloadableContent(
    loadable: Loadable<T>,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    content: @Composable (T) -> Unit,
) {
    if (isEnabled) {
        PullToRefreshBox(
            isRefreshing = loadable is Loadable.Loading,
            onRefresh = onRefresh,
            state = rememberPullToRefreshState(),
            modifier = modifier.fillMaxSize(),
        ) {
            LoadableContent(
                loadable = loadable,
                onRetry = onRetry,
                content = content,
                customLoading = {},
            )
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            LoadableContent(
                loadable = loadable,
                onRetry = onRetry,
                content = content,
            )
        }
    }
}

@Composable
fun <T> LoadableContent(
    loadable: Loadable<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
    customLoading: @Composable () -> Unit = {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    },
) {
    when (loadable) {
        is Loadable.Error -> {
            Column(
                modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = loadable.throwable.message
                        ?: stringResource(R.string.error_message_unknown),
                    color = MaterialTheme.colorScheme.error,
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

        is Loadable.Loading -> {
            customLoading()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomLoadableContentLoadingPreview() {
    ZedgePhotosDetailsTheme {
        LoadableContent(
            loadable = Loadable.Loading,
            onRetry = {},
            content = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReloadableContentLoadingPreview() {
    ZedgePhotosDetailsTheme {
        ReloadableContent(
            loadable = Loadable.Loading,
            onRetry = {},
            onRefresh = {},
            content = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReloadableContentErrorPreview() {
    ZedgePhotosDetailsTheme {
        ReloadableContent(
            loadable = Loadable.Error(
                Throwable("Unable to resolve host TEST. No address and text is long"),
            ),
            onRetry = {},
            onRefresh = {},
            content = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReloadableContentSuccessPreview() {
    ZedgePhotosDetailsTheme {
        ReloadableContent(
            loadable = Loadable.Success("Success Data"),
            onRetry = {},
            onRefresh = {},
            content = { data ->
                Text("Content: $data", modifier = Modifier.fillMaxSize())
            },
        )
    }
}
