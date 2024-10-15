package com.example.pagergallery.repository.local.tables.collection

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CollectionDaoUtil(private val collDao : CollectionDao) {

    //查询
    fun getAllCollection() = collDao.getAllCollections()

    //是否收藏
    suspend fun isCollection(id : Long) = withContext(Dispatchers.IO) {
            collDao.isCollection(id)
        }

    //收藏
    suspend fun insertCollections(vararg collections: Collection){
        withContext(Dispatchers.IO) {
            collDao.insertColl(*collections)
        }
    }

    //删除多个
    suspend fun deleteAll(){
        withContext(Dispatchers.IO) {
            collDao.deleteAllCollections()
        }
    }

    //删除单个
    suspend fun deleteColl(collection: Collection){
        withContext(Dispatchers.IO) {
            collDao.deleteColl(collection)
        }
    }

    //删除单个
    suspend fun deleteCollById(id: Long){
        withContext(Dispatchers.IO) {
            collDao.deleteColl(id)
        }
    }

}