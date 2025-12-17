package com.bookshelf.keeper.data

import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val dao: BookDao
) {
    val allBooks = dao.getAllBooks()
    val allRooms = dao.getAllRooms()
    val allLanguages = dao.getAllLanguages()

    suspend fun addBook(
        title: String,
        authors: String,
        locationLevel1: String,
        language: String
    ) {
        val book = Book(
            title = title,
            authors = authors,
            language = language,
            genre = null,
            locationLevel1 = locationLevel1,
            locationLevel2 = null,
            locationLevel3 = null,
            locationLevel4 = null,
            locationLevel5 = null,
            readingStatus = "not_read"
        )
        dao.insertBook(book)
    }

    fun getBookById(id: Long): Flow<Book?> = dao.getBookById(id)
    suspend fun updateBook(book: Book) = dao.updateBook(book)
    suspend fun deleteBook(book: Book) = dao.deleteBook(book)
}
