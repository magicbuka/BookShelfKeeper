package com.bookshelf.keeper.data

class BookRepository(
    private val dao: BookDao
) {
    val allBooks = dao.getAllBooks()

    suspend fun addBook(title: String, authors: String, locationLevel1: String) {
        val book = Book(
            title = title,
            authors = authors,
            language = "RU",          // временно захардкожено
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
