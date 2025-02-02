package com.example.pagergallery.repository.local.tables.download


class DownLoadDaoUtil(private val downLoadDao: DownLoadDao) {

    //查询
    fun getDownLoads(uid: Int) = downLoadDao.getDownLoads(uid)

    //添加
    suspend fun insert(downLoad: DownLoad) {
        downLoadDao.insertIgnore(downLoad)
    }
    suspend fun insertItemList(vararg downLoad: DownLoad) {
        downLoadDao.insertAllIgnore(*downLoad)
    }

    //删除
    suspend fun deleteDownLoad(downLoad: DownLoad) {
        downLoadDao.delete(downLoad)
    }

    suspend fun deleteAll() {
        downLoadDao.clearDownLoads()

    }

    //更新
    suspend fun update(downLoad: DownLoad) {
        downLoadDao.update(downLoad)

    }

}