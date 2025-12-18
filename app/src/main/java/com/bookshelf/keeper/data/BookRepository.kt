package com.bookshelf.keeper.data

import com.bookshelf.keeper.data.AppDatabase.LocationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(
    private val dao: BookDao,
    private val locationDao: LocationDao
) {
    val allBooks = dao.getAllBooks()
    val allRooms = dao.getAllRooms()
    val allLanguages = dao.getAllLanguages()

    val allRootLocations = locationDao.getRootLocations()
    val allRootLocationNames = allRootLocations.map { list ->
        list.map { it.name }.distinct().sorted()
    }

    suspend fun addBook(
        title: String,
        authors: String,
        locationLevel1: String,
        language: String
    ) {
        val location = getOrCreateRootLocation(locationLevel1)

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
            readingStatus = "not_read",
            locationId = location.id
        )

        dao.insertBook(book)
    }

    fun getBookById(id: Long): Flow<Book?> = dao.getBookById(id)
    suspend fun updateBook(book: Book) {
        val location = getOrCreateRootLocation(book.locationLevel1)
        val bookWithLocation = book.copy(locationId = location.id)
        dao.updateBook(bookWithLocation)
    }
    suspend fun deleteBook(book: Book) = dao.deleteBook(book)
    suspend fun getOrCreateRootLocation(name: String): Location {
        val trimmed = name.trim()
        if (trimmed.isBlank()) {
            return Location(name = "", parentId = null)
        }

        val existing = locationDao.getRootLocationByName(trimmed)
        if (existing != null) return existing

        val newLocation = Location(name = trimmed, parentId = null)
        val newId = locationDao.insertAndReturnId(newLocation)
        return newLocation.copy(id = newId)
    }
}
