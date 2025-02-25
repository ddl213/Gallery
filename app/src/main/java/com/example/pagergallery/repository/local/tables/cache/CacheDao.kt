package com.example.pagergallery.repository.local.tables.cache

import androidx.room.Dao
import androidx.room.Query
import com.example.pagergallery.unit.base.dao.BaseDao
import com.example.pagergallery.unit.util.IConstStringUtil
import kotlinx.coroutines.flow.Flow


@Dao
interface CacheDao : BaseDao<Cache>{
    @Query("DELETE FROM ${IConstStringUtil.TABLE_CACHE}")
    suspend fun deleteAll()

    @Query("SELECT * FROM ${IConstStringUtil.TABLE_CACHE} WHERE user_id = :uid ORDER BY time DESC")
    fun getCaches(uid : Int) : Flow<List<Cache>>

}