package com.example.pagergallery.fragment.home

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pagergallery.repository.Repository

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    //初始化变量
    private val repository = Repository.getInstance(getApplication())

    private val isFirstLoad = mutableListOf(true,true,true,true)
    private val reLoad = mutableStateOf(false)
    val galleryListLiveData = repository.galleryListLiveDate
    val currentTab = mutableStateOf(0)
    val reLoadState = mutableStateOf(false)
    val reFresh = repository.reFresh


    //获取图片
    fun getData() = repository.getPagingData().cachedIn(viewModelScope)

    private fun firstLoad(){
        isFirstLoad[currentTab.value] = false
    }
    fun getIsFirstLoad() : Boolean{
        return isFirstLoad[currentTab.value].also {
            if (it) firstLoad()
        }
    }

    //设置查询的图片类别
    fun setImageTypeStr() {
        repository.setImageTypeStr(list_type[currentTab.value])
    }
    val getImageTypeStr = repository.getPicTypeStr()


    //设置查询字符串
    fun setQuery(queryStr: String?) {
        repository.setQuery(queryStr)
    }
    fun getQuery() = repository.getQuery()

    fun setReLoad(boolean: Boolean) {
        reLoad.value = boolean
    }

    fun getReLoad() = reLoad.value

    fun clearQueryList() {
        repository.clearQueryList()
    }
}