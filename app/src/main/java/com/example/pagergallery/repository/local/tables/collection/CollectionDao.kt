package com.example.pagergallery.repository.local.tables.collection

import androidx.room.Dao
import androidx.room.Query
import com.example.pagergallery.unit.TABLE_COLL_NAME
import com.example.pagergallery.unit.base.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao : BaseDao<Collection>{

    @Query("SELECT * FROM $TABLE_COLL_NAME WHERE user_id = :uid ORDER BY time DESC")
    fun getAllCollections(uid : Int) : Flow<List<Collection>>

    @Query("SELECT EXISTS(SELECT * FROM $TABLE_COLL_NAME WHERE id = :id)")
    suspend fun isCollection(id : Long) : Boolean

    @Query("DELETE FROM $TABLE_COLL_NAME")
    suspend fun deleteAllCollections()

    @Query("DELETE FROM $TABLE_COLL_NAME WHERE id = :id and user_id = :uid")
    suspend fun deleteCollById(id: Long, uid: Int)

}