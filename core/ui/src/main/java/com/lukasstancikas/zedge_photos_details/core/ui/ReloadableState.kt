package com.lukasstancikas.zedge_photos_details.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ReloadableContent(
    loadable: Loadable<T>,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = loadable is Loadable.Loading,
        onRefresh = onRefresh,
        state = rememberPullToRefreshState(),
        modifier = modifier.fillMaxSize(),
    ) {
        when (loadable) {
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
            // might need to iterate on this one to make content visible while refreshing
            is Loadable.Loading -> {}
        }
    }
}
