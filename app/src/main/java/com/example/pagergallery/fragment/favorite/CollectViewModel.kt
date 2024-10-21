package com.example.pagergallery.fragment.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.api.Item
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
        val uid = repository.user.value?.id ?: return
        viewModelScope.launch {
            collectDaoUtil.getAllCollection(uid)
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

    fun clear(){
        viewModelScope.launch {
            collectDaoUtil.deleteAll()
        }
    }

    fun setTitle(title: String){
        repository.setTitle(title)
    }

}