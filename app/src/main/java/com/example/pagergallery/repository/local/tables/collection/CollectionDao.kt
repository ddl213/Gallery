package com.example.pagergallery.repository.local.tables.collection

import androidx.room.Dao
import androidx.room.Query
import com.example.pagergallery.unit.base.dao.BaseDao
import com.example.pagergallery.unit.util.IConstStringUtil
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao : BaseDao<Collection>{

    @Query("SELECT * FROM ${IConstStringUtil.TABLE_COLL_NAME} WHERE user_id = :uid ORDER BY time DESC")
    fun getAllCollections(uid : Int) : Flow<List<Collection>>

    @Query("SELECT EXISTS(SELECT * FROM ${IConstStringUtil.TABLE_COLL_NAME} WHERE id = :id and user_id = :uid)")
    suspend fun isCollection(id : Long,uid: Int) : Boolean

    @Query("DELETE FROM ${IConstStringUtil.TABLE_COLL_NAME}")
    suspend fun deleteAllCollections()

    @Query("DELETE FROM ${IConstStringUtil.TABLE_COLL_NAME} WHERE id = :id and user_id = :uid")
    suspend fun deleteCollById(id: Long, uid: Int)

}