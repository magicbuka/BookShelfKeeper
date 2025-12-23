package com.bookshelf.keeper.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("""
        SELECT * FROM books 
        WHERE title LIKE '%' || :query || '%' 
           OR authors LIKE '%' || :query || '%' 
        ORDER BY title
    """)
    fun searchBooks(query: String): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Long): Flow<Book?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT DISTINCT locationLevel1 FROM books WHERE locationLevel1 != '' ORDER BY locationLevel1 ASC")
    fun getAllRooms(): Flow<List<String>>

    @Query("SELECT DISTINCT language FROM books WHERE language != '' ORDER BY language ASC")
    fun getAllLanguages(): Flow<List<String>>

    @Query(
        "SELECT DISTINCT locationLevel2 " +
                "FROM books " +
                "WHERE locationLevel1 = :room AND locationLevel2 IS NOT NULL AND locationLevel2 != '' " +
                "ORDER BY locationLevel2 COLLATE NOCASE"
    )
    fun getLocationLevel2ForRoom(room: String): Flow<List<String>>
}