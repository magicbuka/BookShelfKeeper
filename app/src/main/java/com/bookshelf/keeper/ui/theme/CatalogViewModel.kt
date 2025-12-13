package com.bookshelf.keeper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelf.keeper.data.AppDatabase
import com.bookshelf.keeper.data.BookRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CatalogViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: BookRepository

    val books: StateFlow<List<com.bookshelf.keeper.data.Book>>

    init {
        val dao = AppDatabase.getDatabase(app).bookDao()
        repo = BookRepository(dao)

        books = repo.allBooks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}

