package com.lukasstancikas.zedge_photos_details.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Wraps the Navigation 3 back stack and provides type-safe navigate/goBack helpers.
 */
class Navigator(private val backStack: NavBackStack<NavKey>) {

    fun navigate(key: NavKey) {
        backStack.add(key)
    }

    fun goBack() {
        if (backStack.isNotEmpty()) backStack.removeLastOrNull()
    }
}
