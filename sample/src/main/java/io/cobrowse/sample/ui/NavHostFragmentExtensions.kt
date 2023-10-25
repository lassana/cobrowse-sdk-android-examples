package io.cobrowse.sample.ui

import androidx.navigation.fragment.NavHostFragment

fun NavHostFragment.popNavigation(): Boolean {
    this.navController.currentBackStackEntry?.let {
        if (it.destination.id != this.navController.graph.startDestinationId) {
            this.navController.popBackStack()
            return true
        }
    }
    return false
}

fun NavHostFragment.canPopNavigation(): Boolean {
    this.navController.currentBackStackEntry?.let {
        return it.destination.id != this.navController.graph.startDestinationId
    }
    return false
}