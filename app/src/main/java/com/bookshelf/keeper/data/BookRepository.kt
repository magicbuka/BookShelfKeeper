package com.bookshelf.keeper.data

class BookRepository(
    private val dao: BookDao
) {
    val allBooks = dao.getAllBooks()

    suspend fun addBook(title: String, authors: String) {
        val book = Book(
            title = title,
            authors = authors,
            language = "RU",          // временно захардкожено
            genre = null,
            locationLevel1 = "Кабинет",
            locationLevel2 = null,
            locationLevel3 = null,
            locationLevel4 = null,
            locationLevel5 = null,
            readingStatus = "not_read"
        )
        dao.insertBook(book)
    }
}
