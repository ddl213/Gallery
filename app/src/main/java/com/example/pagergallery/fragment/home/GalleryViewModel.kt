package com.example.pagergallery.fragment.home

import android.app.Application
import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.api.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    //初始化变量
    private val repository = Repository.getInstance(getApplication())

    private val isFirstLoad = mutableListOf(true, true, true, true)
    private val reLoad = mutableStateOf(false)
    val currentTab = mutableStateOf(0)
    val reLoadState = mutableStateOf(false)

    val recyclerViewState = mutableStateOf<Parcelable?>(null)

    fun getActivityLoadState() = repository.isActivityFirstLoad.value


    fun getNewItemList(type: String): List<Item>? {
        return repository.galleryListLiveDate.value?.get(type)
    }

    //获取图片
    fun getData() = repository.getPagingData().cachedIn(CoroutineScope(Dispatchers.IO))

    private fun firstLoad() {
        isFirstLoad[currentTab.value] = false
    }

    fun getIsFirstLoad(): Boolean {
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