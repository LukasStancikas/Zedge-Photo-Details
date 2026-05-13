package com.lukasstancikas.zedge_photos_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.lukasstancikas.zedge_photos_details.core.navigation.FeatureNavEntryProvider
import com.lukasstancikas.zedge_photos_details.core.navigation.Navigator
import com.lukasstancikas.zedge_photos_details.core.navigation.PhotoListDestination

@Composable
fun ZedgeNavDisplay(navigationProviders: Set<FeatureNavEntryProvider>) {
    val backStack = rememberNavBackStack(PhotoListDestination)
    val navigator = remember(backStack) { Navigator(backStack) }

    NavDisplay(
        backStack = backStack,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider {
            navigationProviders.forEach { provider ->
                with(provider) {
                    registerEntries(navigator)
                }
            }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            // This isolates the ViewModel lifecycle per back stack entry for PhotoDetailsScreen
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}
