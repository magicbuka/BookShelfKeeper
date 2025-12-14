package com.bookshelf.keeper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelf.keeper.data.AppDatabase
import com.bookshelf.keeper.data.BookRepository
import kotlinx.coroutines.launch

class AddBookViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: BookRepository

    init {
        val dao = AppDatabase.getDatabase(app).bookDao()
        repo = BookRepository(dao)
    }

    fun saveBook(title: String, authors: String, locationLevel1: String) {
        viewModelScope.launch {
            repo.addBook(title, authors, locationLevel1)
        }
    }
}
