package com.example.pagergallery.repository.local.tables.collection

class CollectionDaoUtil(private val collDao: CollectionDao) {

    //查询
    fun getAllCollection(uid: Int) = collDao.getAllCollections(uid)

    //是否收藏
    suspend fun isCollection(id: Long) = collDao.isCollection(id)


    //收藏
    suspend fun insertCollections(vararg collections: Collection) {
        collDao.insertColl(*collections)
    }

    //删除多个
    suspend fun deleteAll() {
        collDao.deleteAllCollections()

    }

    //删除单个
    suspend fun deleteColl(collection: Collection) {
        collDao.delete(collection)

    }

    //删除单个
    suspend fun deleteCollById(id: Long,uid: Int) {
        collDao.deleteColl(id,uid)

    }

}