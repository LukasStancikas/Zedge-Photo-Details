package com.lukasstancikas.zedge_photos_details.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingTopAppBar(
    title: String,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val localDensity = LocalDensity.current
    val statusBarDp = with(localDensity) { WindowInsets.statusBars.getTop(localDensity).toDp() }

    TopAppBar(
        title = {
            Text(
                title,
                modifier = Modifier.padding(top = statusBarDp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = { Row(modifier = Modifier.padding(top = statusBarDp)) { actions() } },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        scrollBehavior = topAppBarScrollBehavior,
        expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarDp,
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenScaffold(
    title: String,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    FullScreenScaffold(
        topAppBar = { scrollBehaviour, paddingValues ->
            CollapsingTopAppBar(
                title,
                topAppBarScrollBehavior = scrollBehaviour,
                actions = actions,
                modifier = Modifier
            )
        },
        content = content,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenScaffold(
    topAppBar: @Composable (TopAppBarScrollBehavior, PaddingValues) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    val systemBars = WindowInsets.statusBars
        .union(WindowInsets.navigationBars)
        .union(WindowInsets.displayCutout)
        .asPaddingValues()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topAppBar(scrollBehavior, systemBars) },
    ) { innerPadding ->

        val contentPadding = PaddingValues(
            start = systemBars.calculateStartPadding(layoutDirection),
            top = innerPadding.calculateTopPadding(),
            end = systemBars.calculateEndPadding(layoutDirection),
            bottom = innerPadding.calculateBottomPadding()
        )
        content(contentPadding)
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun FullScreenScaffoldPreview() {
    ZedgePhotosDetailsTheme {
        FullScreenScaffold(
            title = "Preview Title",
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(30) {
                        Text(text = "$it, Content goes here")
                    }
                }

            }
        )
    }
}
