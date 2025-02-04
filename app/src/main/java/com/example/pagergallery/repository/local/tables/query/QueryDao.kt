package com.example.pagergallery.repository.local.tables.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.pagergallery.unit.TABLE_QUERY_NAME
import com.example.pagergallery.unit.base.dao.BaseDao

@Dao
interface QueryDao : BaseDao<HistoryQuery>{

    @Query("DELETE FROM $TABLE_QUERY_NAME")
    suspend fun deleteAllQuery()

    @Query("DELETE FROM $TABLE_QUERY_NAME WHERE id IN (:id)")
    suspend fun deleteQuery(id: Array<Int>)

    @Update
    suspend fun updateDateByStr(query: HistoryQuery)

    @Query("SELECT * FROM $TABLE_QUERY_NAME ORDER BY time DESC")
    fun getAllQuery() : LiveData<List<HistoryQuery>>


}