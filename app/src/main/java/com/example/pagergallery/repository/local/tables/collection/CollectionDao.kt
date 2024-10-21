package com.example.pagergallery.repository.local.tables.collection

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pagergallery.unit.TABLE_COLL_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Query("SELECT * FROM $TABLE_COLL_NAME WHERE user_id = :uid ORDER BY time DESC")
    fun getAllCollections(uid : Int) : Flow<List<Collection>>

    @Query("SELECT EXISTS(SELECT * FROM $TABLE_COLL_NAME WHERE id = :id)")
    suspend fun isCollection(id : Long) : Boolean


    @Query("DELETE FROM $TABLE_COLL_NAME")
    suspend fun deleteAllCollections()

    @Delete
    suspend fun deleteColl(collection: Collection)

    @Query("DELETE FROM $TABLE_COLL_NAME WHERE id = :id")
    suspend fun deleteColl(id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertColl(vararg items : Collection)

}