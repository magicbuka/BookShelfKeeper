package com.bookshelf.keeper.data

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
}
