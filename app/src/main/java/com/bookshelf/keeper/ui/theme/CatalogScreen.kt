package com.bookshelf.keeper.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookshelf.keeper.data.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onAddBookClick: () -> Unit,
    viewModel: CatalogViewModel = viewModel()
) {
    val booksState = viewModel.books.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "BookShelf Keeper") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBookClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить книгу"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val books = booksState.value

            if (books.isEmpty()) {
                Text(
                    text = "Каталог пока пуст.\nСкоро здесь будут книги.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(books) { book ->
                        BookRow(book)
                    }
                }
            }
        }
    }
}

@Composable
private fun BookRow(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = book.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = book.authors,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Язык: ${book.language}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Комната: ${book.locationLevel1}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
