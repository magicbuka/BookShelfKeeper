package com.bookshelf.keeper.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val CATALOG = "catalog"
    const val ADD_BOOK = "add_book"
}

@Composable
fun BookShelfNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.CATALOG
    ) {
        composable(Routes.CATALOG) {
            CatalogScreen(
                onAddBookClick = { navController.navigate(Routes.ADD_BOOK) }
            )
        }
        composable(Routes.ADD_BOOK) {
            AddBookScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
