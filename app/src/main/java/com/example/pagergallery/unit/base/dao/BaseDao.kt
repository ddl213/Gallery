package com.example.pagergallery.unit.base.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BaseDao<T>{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(t: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg t: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAbort(t: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllAbort(vararg t: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(t: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(vararg t: T)

    @Delete
    suspend fun delete(t: T)

    @Delete
    suspend fun deleteRang(vararg query:  T)

    @Update
    suspend fun update(t: T)
}