package com.example.pagergallery.fragment.favorite

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.logD
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CollectViewModel(application: Application) : AndroidViewModel(application) {
    val repository = Repository.getInstance(getApplication())
    private val collectDaoUtil = repository.getCollectionDaoUtil()
    private val _collectList = MutableStateFlow<List<Item>>(listOf())
    val collectListLive : StateFlow<List<Item>> get() = _collectList

    fun getCollect(){
        viewModelScope.launch {
            collectDaoUtil.getAllCollection()
                .flowOn(Dispatchers.IO)
                .collect { list ->
                    list.map {item ->
                        Gson().fromJson(item.hits,Item::class.java)
                    }.apply {
                        _collectList.emit(this)
                    }
                }
        }
    }

    fun setTopAppBarVisible(activity: FragmentActivity, visible : Boolean, canBack : Boolean, color : Int?, title: String = ""){
        repository.setTopAppBarVisible(activity, visible, canBack, color, title)
    }

}