package com.example.pagergallery.repository.local.tables.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CacheDaoUtil(private val cacheDao: CacheDao) {

    //查询
    fun getCaches() = cacheDao.getCaches()

    //添加
    suspend fun insertItemList(vararg cache: Cache) {
        cacheDao.insertCaches(*cache)
    }

    //删除
    suspend fun deleteCache(cache: Cache) {
        cacheDao.deleteCache(cache)
    }

    suspend fun deleteAll() {
        cacheDao.clearCaches()

    }

    //更新
    suspend fun update(cache: Cache) {
        cacheDao.updateCache(cache)

    }

}