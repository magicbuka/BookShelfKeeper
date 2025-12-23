package com.bookshelf.keeper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Database(
    entities = [Book::class, Location::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun locationDao(): LocationDao

    @Dao
    abstract class LocationDao {
        @Query("SELECT * FROM locations WHERE parentId IS NULL ORDER BY name")
        abstract fun getRootLocations(): Flow<List<Location>>

        @Query("SELECT * FROM locations WHERE parentId = :parentId ORDER BY name")
        abstract fun getChildLocations(parentId: Long): Flow<List<Location>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        abstract suspend fun insertAndReturnId(location: Location): Long

        @Query("SELECT * FROM locations WHERE name = :name AND parentId IS NULL LIMIT 1")
        abstract suspend fun getRootLocationByName(name: String): Location?

        @Query(
            "SELECT * FROM locations " +
                    "WHERE name = :name AND " +
                    "((:parentId IS NULL AND parentId IS NULL) OR parentId = :parentId) " +
                    "LIMIT 1"
        )
        abstract suspend fun getLocationByNameAndParent(
            name: String,
            parentId: Long?
        ): Location?

        @Query("SELECT * FROM locations WHERE id = :id")
        abstract suspend fun getLocationById(id: Long): Location?
    }

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bookshelf_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
