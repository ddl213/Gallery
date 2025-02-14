package com.example.pagergallery.fragment.me.download

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.enmu.FragmentFromEnum
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.manager.DownloadManager
import com.example.pagergallery.unit.util.LogUtil
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class DownLoadViewModel(application: Application) : AndroidViewModel(application) {
    val repository = Repository.getInstance(getApplication())

    fun setTitle(title: String) {
        repository.setTitle(title)
    }

    private val cacheDaoUtil = repository.getCacheDaoUtil()
    private val _cacheList = MutableStateFlow<List<Item>>(listOf())
    val cacheList: StateFlow<List<Item>> get() = _cacheList

    private fun getCaches() {
        val uid = repository.user.value?.id ?: return
        viewModelScope.launch {
            cacheDaoUtil.getCaches(uid)
                .flowOn(Dispatchers.IO)
                .collect { list ->
                    list.map {
                        it.item
                    }.apply {
                        _cacheList.emit(this)
                    }
                }
        }
    }

    fun clear() {
        viewModelScope.launch {
            cacheDaoUtil.deleteAll()
        }
    }

    //获取下载图片
    private val imagePath = "${MediaStore.Images.Media.EXTERNAL_CONTENT_URI}${File.separator}"
    private val projection = arrayOf(MediaStore.Images.Media._ID)

    @RequiresApi(Build.VERSION_CODES.Q)
    private val select =
        "${MediaStore.Images.Media.RELATIVE_PATH} LIKE 'Pictures/${repository.user.value?.account}_gallery%' AND ${MediaStore.Images.Media.DISPLAY_NAME} LIKE 'pixabay_%' AND ${MediaStore.Images.Media.MIME_TYPE} =?"
    private val selectArgs = arrayOf("image/png")
    private val sortOrder = MediaStore.Images.Media.DATE_ADDED

    //查询下载图片
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFilePath(application: FragmentActivity) : List<Uri>?{
        val cursor = application.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            select,
            selectArgs,
            sortOrder
        ) ?: return null

        if (cursor.count == 0) return null

        val list = mutableListOf<Uri>()
        //获取列_ID的索引
        val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        //遍历下载图片
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumnIndex)
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)
            list.add(uri)
        }
        //关闭接口
        cursor.close()
        return list
    }

    private val _downLoadViewList = MutableStateFlow<List<Item>>(listOf())
    val downLoadViewList: StateFlow<List<Item>> get() = _downLoadViewList

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getDownload(application: FragmentActivity) {
        val uid = repository.user.value?.id ?: return
        viewModelScope.launch {
            repository.getDownLoadDaoUtil().getDownLoads(uid)
                .flowOn(Dispatchers.IO)
                .collect {collectList ->
                    if (_downLoadViewList.value.isNotEmpty() && _downLoadViewList.value.size == collectList.size) return@collect

                    var count = 0
                    _downLoadViewList.value = collectList.map {
                        LogUtil.d("下载： ${it.item.localUrl}::${count++}")
                        it.item
                    }

//                    _downLoadViewList.update {
//                        for (index in itemList.indices){
//                            itemList[index].webFormatURL = urlList[index]
//                        }
//                        itemList
//                    }
                }
        }

    }

    private val collectDaoUtil = repository.getCollectionDaoUtil()
    private val _collectList = MutableStateFlow<List<Item>>(listOf())
    val collectListLive: StateFlow<List<Item>> get() = _collectList

    fun getCollect() {
        val uid = repository.user.value?.id ?: return
        viewModelScope.launch {
            collectDaoUtil.getAllCollections(uid)
                .flowOn(Dispatchers.IO)
                .collect { list ->
                    list.map { item ->
                        Gson().fromJson(item.hits, Item::class.java)
                    }.apply {
                        _collectList.emit(this)
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getData(from : FragmentFromEnum, application: FragmentActivity){
        when(from){
            FragmentFromEnum.DownLoad -> getDownload(application)
            FragmentFromEnum.Collect -> getCollect()
            FragmentFromEnum.History -> getCaches()

            else -> {}
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updateData(from : FragmentFromEnum, application: FragmentActivity){
        val urlList = getFilePath(application)
    }

}