package com.example.pagergallery.repository.local.tables.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pagergallery.unit.TABLE_CACHE
import kotlinx.coroutines.flow.Flow


@Dao
interface CacheDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaches(vararg cache: Cache)

    @Delete
    suspend fun deleteCache(cache: Cache)
    @Query("DELETE FROM $TABLE_CACHE")
    suspend fun clearCaches()

    @Update
    suspend fun updateCache(cache: Cache)

    @Query("SELECT * FROM $TABLE_CACHE ORDER BY id")
    fun getCaches() : Flow<List<Cache>>

}