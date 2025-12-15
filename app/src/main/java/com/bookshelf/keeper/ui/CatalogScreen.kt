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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import com.bookshelf.keeper.data.Book

import com.bookshelf.keeper.ui.MAX_LANG_FILTER_CHIPS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onAddBookClick: () -> Unit,
    viewModel: CatalogViewModel = viewModel()
) {
    val booksState = viewModel.filteredBooks.collectAsState()
    val languagesState = viewModel.languages.collectAsState()
    val selectedLanguageState = viewModel.selectedLanguage.collectAsState()


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
            val languages = languagesState.value
            val selectedLanguage = selectedLanguageState.value

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (languages.size in 1..MAX_LANG_FILTER_CHIPS) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Чип "All"
                        FilterChip(
                            selected = selectedLanguage == null,
                            onClick = { viewModel.onLanguageSelected(null) },
                            label = { Text("All") }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        languages.forEach { language ->
                            FilterChip(
                                selected = selectedLanguage == language,
                                onClick = { viewModel.onLanguageSelected(language) },
                                label = { Text(language) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (books.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Каталог пока пуст.\nСкоро здесь будут книги.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
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
