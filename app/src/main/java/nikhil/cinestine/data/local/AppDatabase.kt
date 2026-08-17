package nikhil.cinestine.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FavouriteEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS favourites_new (
                        id TEXT NOT NULL,
                        mediaType TEXT NOT NULL,
                        title TEXT NOT NULL,
                        originalTitle TEXT NOT NULL,
                        overview TEXT NOT NULL,
                        posterPath TEXT NOT NULL,
                        backdropPath TEXT NOT NULL,
                        voteAverage REAL NOT NULL,
                        releaseDate TEXT NOT NULL,
                        popularity TEXT NOT NULL,
                        voteCount TEXT NOT NULL,
                        originalLanguage TEXT NOT NULL,
                        adult TEXT NOT NULL,
                        PRIMARY KEY(id, mediaType)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO favourites_new (
                        id, mediaType, title, originalTitle, overview, posterPath, backdropPath,
                        voteAverage, releaseDate, popularity, voteCount, originalLanguage, adult
                    )
                    SELECT
                        id, 'MOVIE', title, originalTitle, overview, posterPath, backdropPath,
                        voteAverage, releaseDate, popularity, voteCount, originalLanguage, adult
                    FROM favourites
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE favourites")
                db.execSQL("ALTER TABLE favourites_new RENAME TO favourites")
            }
        }
    }
}
