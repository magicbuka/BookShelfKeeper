package com.bookshelf.keeper.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Query("SELECT DISTINCT locationLevel1 FROM books ORDER BY locationLevel1")
    fun getAllRooms(): kotlinx.coroutines.flow.Flow<List<String>>

    @Query("SELECT DISTINCT language FROM books ORDER BY language")
    fun getAllLanguages(): kotlinx.coroutines.flow.Flow<List<String>>
}
