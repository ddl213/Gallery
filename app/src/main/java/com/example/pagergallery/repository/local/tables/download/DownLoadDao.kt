package com.example.pagergallery.repository.local.tables.download

import androidx.room.Dao
import androidx.room.Query
import com.example.pagergallery.unit.base.dao.BaseDao
import com.example.pagergallery.unit.util.IConstStringUtil
import kotlinx.coroutines.flow.Flow

@Dao
interface DownLoadDao:BaseDao<DownLoad>{

    @Query("DELETE FROM ${IConstStringUtil.TABLE_DOWNLOAD}")
    suspend fun clearDownLoads()

    @Query("SELECT * FROM ${IConstStringUtil.TABLE_DOWNLOAD} WHERE user_id = :uid ORDER BY time DESC")
    fun getDownLoads(uid : Int) : Flow<List<DownLoad>>

}