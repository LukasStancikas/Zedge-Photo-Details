package com.lukasstancikas.zedge_photos_details.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingTopAppBar(
    title: String,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary
        ),
        scrollBehavior = topAppBarScrollBehavior,
        expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarDp,
        modifier = modifier
            .fillMaxWidth()
            .consumeWindowInsets(WindowInsets.statusBars)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenScaffold(
    title: String,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
) {
    FullScreenScaffold({ CollapsingTopAppBar(title, it) }, content, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenScaffold(
    topAppBar: @Composable (TopAppBarScrollBehavior) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topAppBar(scrollBehavior) },
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val systemBars = WindowInsets.statusBars
            .union(WindowInsets.navigationBars)
            .union(WindowInsets.displayCutout)
            .asPaddingValues()
        val contentPadding = PaddingValues(
            start = systemBars.calculateStartPadding(layoutDirection),
            top = innerPadding.calculateTopPadding(),
            end = systemBars.calculateEndPadding(layoutDirection),
            bottom = systemBars.calculateBottomPadding()
        )
        content(contentPadding)
    }
}
