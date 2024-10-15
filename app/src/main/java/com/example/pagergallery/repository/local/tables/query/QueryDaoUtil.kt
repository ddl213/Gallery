package com.example.pagergallery.repository.local.tables.query

import com.example.pagergallery.unit.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueryDaoUtil(private val queryDao: QueryDao) {

    //查询
    fun getAllQuery() = queryDao.getAllQuery()

    //收藏
    suspend fun insertQuery(items: HistoryQuery) {
        withContext(Dispatchers.IO) {
            queryDao.insertQuery(items)
        }
    }

    //删除多个
    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            queryDao.deleteAllQuery()
        }
    }

    //删除单个
    suspend fun deleteQuery(vararg query: HistoryQuery) {
        withContext(Dispatchers.IO) {
            queryDao.deleteQuery(*query)
        }
    }

    //更具查询字段修改时间
    suspend fun updateDateByStr(query: HistoryQuery) {
        withContext(Dispatchers.IO) {
            queryDao.updateDateByStr(query)
        }
    }
}