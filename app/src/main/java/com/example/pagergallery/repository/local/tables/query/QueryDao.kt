package com.example.pagergallery.repository.local.tables.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pagergallery.unit.TABLE_QUERY_NAME

@Dao
interface QueryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(item : HistoryQuery)

    @Query("DELETE FROM $TABLE_QUERY_NAME")
    suspend fun deleteAllQuery()

    @Delete
    suspend fun deleteQuery(vararg query: HistoryQuery)

    @Update
    suspend fun updateDateByStr(query: HistoryQuery)

    @Query("SELECT * FROM $TABLE_QUERY_NAME ORDER BY time DESC")
    fun getAllQuery() : LiveData<List<HistoryQuery>>


}