package com.example.pagergallery.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pagergallery.repository.local.tables.cache.Cache
import com.example.pagergallery.repository.local.tables.cache.CacheDao
import com.example.pagergallery.repository.local.tables.cache.ItemConverter
import com.example.pagergallery.repository.local.tables.collection.Collection
import com.example.pagergallery.repository.local.tables.collection.CollectionDao
import com.example.pagergallery.repository.local.tables.query.HistoryQuery
import com.example.pagergallery.repository.local.tables.query.QueryDao
import com.example.pagergallery.repository.local.tables.user.User
import com.example.pagergallery.repository.local.tables.user.UserDao
import com.example.pagergallery.unit.TABLE_COLL_NAME
import com.example.pagergallery.unit.TABLE_QUERY_NAME

@Database(
    entities = [Collection::class, HistoryQuery::class, Cache::class, User::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(ItemConverter::class)
abstract class GalleryDatabase : RoomDatabase() {

    companion object {
        private const val DB_NAME = "gallery_db"
        private var instance: GalleryDatabase? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                Room.databaseBuilder(context, GalleryDatabase::class.java, DB_NAME)
                    .addMigrations(MIGRATION_7_8)
                    .build().also { instance = it }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALERT TABLE $TABLE_QUERY_NAME DROP COLUM date")
                db.execSQL("ALTER TABLE $TABLE_QUERY_NAME ADD date LONG")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE $TABLE_QUERY_NAME DROP COLUMN date")
                db.execSQL("ALTER TABLE $TABLE_QUERY_NAME ADD time LONG")
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE $TABLE_COLL_NAME")
                db.execSQL(
                    "CREATE TABLE $TABLE_COLL_NAME (" +
                            "id INTEGER NOT NULL PRIMARY KEY," +
                            "hits TEXT NOT NULL )"
                )
            }
        }
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE 'table_cache' (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "item TEXT NOT NULL," +
                            "type TEXT NOT NULL UNIQUE)"
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_table_cache_type ON table_cache (type)")

                db.execSQL(
                    "CREATE TABLE 'table_user' (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "account INTEGER NOT NULL UNIQUE," +
                            "pwd TEXT NOT NULL," +
                            "name TEXT," +
                            "phone INTEGER NOT NULL UNIQUE," +
                            "sex TEXT ," +
                            "birthday TEXT," +
                            "picture TEXT)"
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_table_user_account_phone ON table_user (account,phone)")

            }
        }
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE table_cache")
                db.execSQL(
                    "CREATE TABLE 'table_cache' (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "item TEXT NOT NULL )"
                )
            }
        }
        private val MIGRATION_7_8 = object : Migration(7,8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE table_cache")
                db.execSQL(
                    "CREATE TABLE 'table_cache' (" +
                            "id INTEGER NOT NULL PRIMARY KEY," +
                            "item TEXT NOT NULL," +
                            "time INTEGER NOT NULL )"
                )
            }
        }
    }

    abstract fun getCollectionDao(): CollectionDao
    abstract fun getQueryDao(): QueryDao
    abstract fun getCacheDao(): CacheDao
    abstract fun getUserDao(): UserDao

}