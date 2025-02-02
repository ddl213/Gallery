package com.example.pagergallery.repository.local.tables.download

import androidx.room.Dao
import androidx.room.Query
import com.example.pagergallery.unit.TABLE_DOWNLOAD
import com.example.pagergallery.unit.base.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface DownLoadDao:BaseDao<DownLoad>{

    @Query("DELETE FROM $TABLE_DOWNLOAD")
    suspend fun clearDownLoads()

    @Query("SELECT * FROM $TABLE_DOWNLOAD WHERE user_id = :uid ORDER BY time DESC")
    fun getDownLoads(uid : Int) : Flow<List<DownLoad>>

}