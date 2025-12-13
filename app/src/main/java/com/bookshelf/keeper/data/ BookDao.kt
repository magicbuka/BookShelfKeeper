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

    // Статистика по языкам
    @Query("SELECT language AS key, COUNT(*) AS count FROM books GROUP BY language")
    fun getLanguageStats(): Flow<List<KeyCount>>

    // Статистика по комнатам (Level 1)
    @Query("SELECT locationLevel1 AS key, COUNT(*) AS count FROM books GROUP BY locationLevel1")
    fun getRoomStats(): Flow<List<KeyCount>>
}

data class KeyCount(
    val key: String,
    val count: Int
)

