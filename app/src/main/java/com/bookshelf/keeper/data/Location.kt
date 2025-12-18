package com.bookshelf.keeper.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "locations",
    indices = [Index(value = ["parentId"])]
)
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,           // "Шкаф 1", "Полка А" (2-15 символов)
    val parentId: Long? = null  // null = Level 1 (корень), else → child Level
)