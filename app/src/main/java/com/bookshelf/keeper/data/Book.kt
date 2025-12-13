package com.bookshelf.keeper.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val authors: String,
    val language: String,      // "RU", "EN", "ZH"
    val genre: String? = null,

    // ГИБКАЯ ЛОКАЦИЯ: 5 уровней
    val locationLevel1: String,   // ОБЯЗАТЕЛЬНО: Комната
    val locationLevel2: String?,  // Шкаф / Полка (если есть)
    val locationLevel3: String?,  // Полка / Ряд
    val locationLevel4: String?,  // Ряд / Позиция
    val locationLevel5: String?,  // Позиция / доп. деталь

    val readingStatus: String     // "not_read", "reading", "read"
)