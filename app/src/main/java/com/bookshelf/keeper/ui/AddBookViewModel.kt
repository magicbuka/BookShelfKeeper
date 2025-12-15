package com.bookshelf.keeper.ui
import com.bookshelf.keeper.data.LanguageItem
import com.bookshelf.keeper.data.Iso639_1Languages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelf.keeper.data.AppDatabase
import com.bookshelf.keeper.data.BookRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf




class AddBookViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: BookRepository

    val rooms: StateFlow<List<String>>

    // Уже использованные коды языков из БД
    val usedLanguages: StateFlow<List<String>>
    // Список подсказок: сначала языки, уже встречающиеся в БД, затем остальные из справочника
    val languageSuggestions: StateFlow<List<LanguageItem>>

    init {
        val dao = AppDatabase.getDatabase(app).bookDao()
        repo = BookRepository(dao)

        rooms = repo.allRooms.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

        usedLanguages = repo.allLanguages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

        languageSuggestions = combine(
            usedLanguages,
            flowOf(Iso639_1Languages)
        ) { usedCodes, allItems ->
            val usedSet = usedCodes.map { it.lowercase() }.toSet()
            val usedItems = allItems.filter { it.code.lowercase() in usedSet }
            val otherItems = allItems.filterNot { it.code.lowercase() in usedSet }
            usedItems + otherItems
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    }

    fun saveBook(
        title: String,
        authors: String,
        locationLevel1: String,
        language: String
    ) {
        viewModelScope.launch {
            repo.addBook(title, authors, locationLevel1, language)
        }
    }
}
