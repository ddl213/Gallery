package com.example.pagergallery.repository.local.tables.cache

class CacheDaoUtil(private val cacheDao: CacheDao) {

    //查询
    fun getCaches(uid : Int) = cacheDao.getCaches(uid)

    //添加
    suspend fun insertItemList(vararg cache: Cache) {
        cacheDao.insertAll(*cache)
    }

    //删除
    suspend fun deleteCache(cache: Cache) {
        cacheDao.delete(cache)
    }

    suspend fun deleteAll() {
        cacheDao.clearCaches()

    }

    //更新
    suspend fun update(cache: Cache) {
        cacheDao.update(cache)

    }

}