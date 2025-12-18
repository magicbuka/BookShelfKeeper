package com.bookshelf.keeper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelf.keeper.data.AppDatabase
import com.bookshelf.keeper.data.Book
import com.bookshelf.keeper.data.BookRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BookDetailsViewModel(
    app: Application,
    bookId: Long
) : AndroidViewModel(app) {

    private val repo: BookRepository

    val book: StateFlow<Book?>

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted

    init {
        val db = AppDatabase.getDatabase(app)
        val dao = db.bookDao()
        val locationDao = db.locationDao()
        repo = BookRepository(dao, locationDao)

        book = repo.getBookById(bookId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
    }

    fun deleteCurrentBook() {
        val current = book.value ?: return
        viewModelScope.launch {
            repo.deleteBook(current)
            _isDeleted.value = true
        }
    }
}

@Suppress("UNCHECKED_CAST")
class BookDetailsViewModelFactory(
    private val app: Application,
    private val bookId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookDetailsViewModel(app = app, bookId = bookId) as T
    }
}