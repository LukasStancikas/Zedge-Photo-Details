package com.lukasstancikas.zedge_photos_details.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Contract that each feature module implements to register its own navigation entries
 * into the app-level
 *
 * The app module collects all implementations via Hilt
 *
 * Features receive a [Navigator] to push/pop destinations without depending on
 * each other directly.
 */
interface FeatureNavEntryProvider {
    fun EntryProviderScope<NavKey>.registerEntries(navigator: Navigator)
}
