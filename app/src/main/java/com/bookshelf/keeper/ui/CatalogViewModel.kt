package com.bookshelf.keeper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelf.keeper.data.AppDatabase
import com.bookshelf.keeper.data.BookRepository
import com.bookshelf.keeper.data.Book
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine


class CatalogViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: BookRepository

    val books: StateFlow<List<Book>>

    // выбранный язык: null = без фильтра
    private val _selectedLanguage = MutableStateFlow<String?>(null)
    val selectedLanguage: StateFlow<String?> = _selectedLanguage

    // список уникальных языков из каталога
    val languages: StateFlow<List<String>>

    // отфильтрованные книги
    val filteredBooks: StateFlow<List<Book>>

    val rooms: StateFlow<List<String>>

    init {
        val db = AppDatabase.getDatabase(app)
        val dao = db.bookDao()
        val locationDao = db.locationDao()
        repo = BookRepository(dao, locationDao)

        books = repo.allBooks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        languages = books
            .map { list -> list.map { it.language }.distinct().sorted() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        filteredBooks = combine(books, _selectedLanguage) { list, lang ->
            if (lang == null) list else list.filter { it.language == lang }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        rooms = books
            .map { list ->
                list.map { it.locationLevel1 }
                    .distinct()
                    .sorted()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun onLanguageSelected(language: String?) {
        _selectedLanguage.value = language
    }
}

