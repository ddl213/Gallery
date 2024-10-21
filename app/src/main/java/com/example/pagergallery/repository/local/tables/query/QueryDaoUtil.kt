package com.example.pagergallery.repository.local.tables.query

class QueryDaoUtil(private val queryDao: QueryDao) {

    //查询
    fun getAllQuery() = queryDao.getAllQuery()

    //收藏
    suspend fun insertQuery(items: HistoryQuery) {
        queryDao.insertQuery(items)
    }

    //删除多个
    suspend fun deleteAll() {
        queryDao.deleteAllQuery()
    }

    //删除单个
    suspend fun deleteQuery(vararg query: HistoryQuery) {
        queryDao.deleteQuery(*query)
    }

    //更具查询字段修改时间
    suspend fun updateDateByStr(query: HistoryQuery) {
        queryDao.updateDateByStr(query)
    }
}