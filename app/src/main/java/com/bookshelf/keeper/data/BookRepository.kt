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

    fun getLevel2SuggestionsForRoom(room: String): Flow<List<String>> {
        return dao.getLocationLevel2ForRoom(room)
    }

    suspend fun addBook(
        title: String,
        authors: String,
        locationLevel1: String,
        language: String,
        locationLevel2: String? = null,
        locationLevel3: String? = null,
        locationLevel4: String? = null,
        locationLevel5: String? = null
    ) {
        // Строим путь до максимально указанного уровня
        val finalLocation = getOrCreateLocationPath(
            level1 = locationLevel1,
            level2 = locationLevel2,
            level3 = locationLevel3,
            level4 = locationLevel4,
            level5 = locationLevel5
        )

        val book = Book(
            title = title,
            authors = authors,
            language = language,
            genre = null,
            locationLevel1 = locationLevel1,
            locationLevel2 = locationLevel2,
            locationLevel3 = locationLevel3,
            locationLevel4 = locationLevel4,
            locationLevel5 = locationLevel5,
            readingStatus = "not_read",
            locationId = finalLocation.id
        )

        dao.insertBook(book)
    }

    fun getBookById(id: Long): Flow<Book?> = dao.getBookById(id)
    suspend fun updateBook(book: Book) {
        val finalLocation = getOrCreateLocationPath(
            level1 = book.locationLevel1,
            level2 = book.locationLevel2,
            level3 = book.locationLevel3,
            level4 = book.locationLevel4,
            level5 = book.locationLevel5
        )
        val bookWithLocation = book.copy(locationId = finalLocation.id)
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
    private suspend fun getOrCreateLocationPath(
        level1: String,
        level2: String?,
        level3: String?,
        level4: String?,
        level5: String?
    ): Location {
        val root = getOrCreateRootLocation(level1)

        var currentParent: Location = root

        fun normalizeOrNull(value: String?): String? {
            val trimmed = value?.trim()
            return if (trimmed.isNullOrEmpty()) null else trimmed
        }

        val levels = listOf(
            normalizeOrNull(level2),
            normalizeOrNull(level3),
            normalizeOrNull(level4),
            normalizeOrNull(level5)
        )

        for (name in levels) {
            if (name == null) continue

            val existing = locationDao.getLocationByNameAndParent(
                name = name,
                parentId = currentParent.id
            )
            if (existing != null) {
                currentParent = existing
                continue
            }

            val newLocation = Location(
                name = name,
                parentId = currentParent.id
            )
            val newId = locationDao.insertAndReturnId(newLocation)
            currentParent = newLocation.copy(id = newId)
        }

        return currentParent
    }
}
