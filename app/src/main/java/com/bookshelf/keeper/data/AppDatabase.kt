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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bookshelf.keeper.data.Location


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

        @Query("SELECT * FROM locations WHERE id = :id")
        abstract suspend fun getLocationById(id: Long): Location?
    }

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Создаём новую таблицу locations
                database.execSQL("""
                    CREATE TABLE locations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        parentId INTEGER
                    )
                """.trimIndent())

                database.execSQL("""
                    CREATE INDEX index_locations_parentId
                    ON locations(parentId)
                """.trimIndent())

                // 2. Переносим locationLevel1 → locations (все как Level 1, parentId=null)
                database.execSQL("""
                    INSERT INTO locations (name)
                    SELECT DISTINCT locationLevel1 
                    FROM books 
                    WHERE locationLevel1 IS NOT NULL AND locationLevel1 != ''
                """.trimIndent())

                // 3. Добавляем locationId в books
                database.execSQL("ALTER TABLE books ADD COLUMN locationId INTEGER")

                // 4. Заполняем locationId по старому locationLevel1
                database.execSQL("""
                    UPDATE books 
                    SET locationId = (
                        SELECT id FROM locations 
                        WHERE locations.name = books.locationLevel1 
                        LIMIT 1
                    )
                    WHERE locationLevel1 IS NOT NULL AND locationLevel1 != ''
                """.trimIndent())

                // 5. Удаляем старое поле
                database.execSQL("""
                    CREATE TABLE books_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        author TEXT NOT NULL,
                        languageCode TEXT NOT NULL DEFAULT 'ru',
                        type TEXT NOT NULL DEFAULT 'book',
                        isbn TEXT,
                        series TEXT,
                        seriesNumber INTEGER DEFAULT 0,
                        locationId INTEGER,
                        createdAt INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO books_new (id, title, author, languageCode, type, isbn, series, seriesNumber, locationId, createdAt)
                    SELECT id, title, author, languageCode, type, isbn, series, seriesNumber, locationId, createdAt
                    FROM books
                """.trimIndent())

                database.execSQL("DROP TABLE books")
                database.execSQL("ALTER TABLE books_new RENAME TO books")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bookshelf_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
