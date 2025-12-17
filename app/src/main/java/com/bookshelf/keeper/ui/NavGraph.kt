package com.bookshelf.keeper.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val CATALOG = "catalog"
    const val ADD_BOOK = "add_book"
    const val BOOK_DETAILS = "book/{bookId}"
    const val EDIT_BOOK = "edit_book/{bookId}"
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
                onAddBookClick = { navController.navigate(Routes.ADD_BOOK) },
                onBookClick = { bookId ->
                    navController.navigate("book/$bookId")
                }
            )
        }

        composable(Routes.ADD_BOOK) {
            AddBookScreen(
                onBackClick = { navController.popBackStack() },
                bookId = null
            )
        }

        composable(
            route = Routes.BOOK_DETAILS,
            arguments = listOf(
                navArgument("bookId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val bookId: Long = backStackEntry.arguments?.getLong("bookId") ?: 0L
            BookDetailsScreen(
                bookId = bookId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate("edit_book/$id")
                }
            )
        }

        composable(
            route = Routes.EDIT_BOOK,
            arguments = listOf(
                navArgument("bookId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val bookId: Long = backStackEntry.arguments?.getLong("bookId") ?: 0L
            AddBookScreen(
                onBackClick = { navController.popBackStack() },
                bookId = bookId
            )
        }
    }
}
