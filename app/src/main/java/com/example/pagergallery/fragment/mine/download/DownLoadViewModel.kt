package com.example.pagergallery.fragment.mine.download

import android.app.Application
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.repository.local.tables.collection.Collection
import com.example.pagergallery.unit.logD
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
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

    fun getCaches() {
        viewModelScope.launch {
            cacheDaoUtil.getCaches()
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

    fun clear(){
        viewModelScope.launch {
            cacheDaoUtil.deleteAll()
        }
    }

    //获取下载图片
    private val _downLoadViewList = MutableStateFlow<List<String>>(listOf())
    val downLoadViewList: StateFlow<List<String>> get() = _downLoadViewList
    private val imagePath = "${MediaStore.Images.Media.EXTERNAL_CONTENT_URI}${File.separator}"
    private val projection = arrayOf(MediaStore.Images.Media._ID)

    @RequiresApi(Build.VERSION_CODES.Q)
    private val select =
        "${MediaStore.Images.Media.RELATIVE_PATH} LIKE 'Pictures/${repository.user.value?.account}_gallery%' AND ${MediaStore.Images.Media.DISPLAY_NAME} LIKE 'pixabay_%' AND ${MediaStore.Images.Media.MIME_TYPE} =?"
    private val selectArgs = arrayOf("image/png")
    private val sortOrder = MediaStore.Images.Media.DATE_ADDED

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFilePath(application: FragmentActivity) {
        val cursor = application.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            select,
            selectArgs,
            sortOrder
        ) ?: return

        if (cursor.count == 0) return

        val list = mutableListOf<String>()
        while (cursor.moveToNext()) {
            cursor.getString(0).apply {
                val path = imagePath + this
                list.add(path)
                logD("getFilePath: $path")
            }
        }
        cursor.close()
        _downLoadViewList.value = list
    }

    private val collectDaoUtil = repository.getCollectionDaoUtil()
    private val _collectList = MutableStateFlow<List<Item>>(listOf())
    val collectListLive: StateFlow<List<Item>> get() = _collectList

    fun getCollect() {
        viewModelScope.launch {
            collectDaoUtil.getAllCollection()
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

}