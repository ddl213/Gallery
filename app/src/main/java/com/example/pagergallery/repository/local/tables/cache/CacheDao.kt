package com.example.pagergallery.repository.local.tables.cache

import androidx.room.Dao
import androidx.room.Query
import com.example.pagergallery.unit.TABLE_CACHE
import com.example.pagergallery.unit.base.dao.BaseDao
import kotlinx.coroutines.flow.Flow


@Dao
interface CacheDao : BaseDao<Cache>{
    @Query("DELETE FROM $TABLE_CACHE")
    suspend fun deleteAll()

    @Query("SELECT * FROM $TABLE_CACHE WHERE user_id = :uid ORDER BY time DESC")
    fun getCaches(uid : Int) : Flow<List<Cache>>

}