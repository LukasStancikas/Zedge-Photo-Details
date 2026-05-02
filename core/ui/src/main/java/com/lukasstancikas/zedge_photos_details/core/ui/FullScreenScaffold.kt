package com.lukasstancikas.zedge_photos_details.core.ui

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lukasstancikas.zedge_photos_details.core.ui.theme.ZedgePhotosDetailsTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingTopAppBar(
    title: String,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    systemBarsPadding: PaddingValues = PaddingValues(),
    actions: @Composable RowScope.() -> Unit = {},
    onBackClick: () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    TopAppBar(
        title = {
            Text(
                title,
                modifier = Modifier.padding(top = systemBarsPadding.calculateTopPadding()),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = {
            Row(
                modifier = Modifier.padding(
                    end = systemBarsPadding.calculateEndPadding(layoutDirection),
                    top = systemBarsPadding.calculateTopPadding()
                )
            ) {
                actions()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        scrollBehavior = topAppBarScrollBehavior,
        expandedHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight + systemBarsPadding.calculateTopPadding(),
        windowInsets = WindowInsets(0),
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(
                    start = systemBarsPadding.calculateStartPadding(layoutDirection),
                    top = systemBarsPadding.calculateTopPadding()
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = stringResource(R.string.menu_back)
                )
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenScaffold(
    title: String,
    content: @Composable (systemBars: PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    onBackClick: (() -> Unit) = {}
) {
    val layoutDirection = LocalLayoutDirection.current
    val systemBars = WindowInsets.statusBars
        .union(WindowInsets.navigationBars)
        .union(WindowInsets.displayCutout)
        .asPaddingValues()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            CollapsingTopAppBar(
                title = title,
                topAppBarScrollBehavior = scrollBehavior,
                actions = actions,
                onBackClick = onBackClick,
                systemBarsPadding = systemBars
            )
        },
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        val contentPadding = PaddingValues(
            start = systemBars.calculateStartPadding(layoutDirection),
            top = innerPadding.calculateTopPadding(),
            end = systemBars.calculateEndPadding(layoutDirection),
            bottom = 0.dp
        )
        Box(Modifier.padding(contentPadding)) {
            content(systemBars)
        }
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
                    items(35) {
                        Text(text = "$it, Content goes here")
                    }
                }
            }
        )
    }
}
