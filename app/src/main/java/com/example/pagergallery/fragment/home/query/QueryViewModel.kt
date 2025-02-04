package com.example.pagergallery.fragment.home.query

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.local.tables.query.HistoryQuery
import kotlinx.coroutines.launch

class QueryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(getApplication())
    private val queryDaoUtil = repository.getQueryDaoUtil()

    private val _queryListLiveData = MutableLiveData<MutableList<HistoryQuery>>()
    private val listDelete = mutableListOf<Int>()

    /**设置和删除查询项*/
    fun setQueryList(list: List<HistoryQuery>){
        _queryListLiveData.value = list.toMutableList()
    }

    fun setDelete(index: Int) {
        _queryListLiveData.value?.get(index)?.id.let { listDelete.add(it ?: -1) }
        _queryListLiveData.value?.removeAt(index)
    }

    //记录搜索字段
    fun setQuery(str: String) {
        repository.setQuery(str)
    }

    fun getQuery() :String?{
        return repository.getQuery()
    }

    //增删改查
    fun insertQuery(query: String) {
        viewModelScope.launch {
            queryDaoUtil.insert(HistoryQuery(null, query, System.currentTimeMillis()))
        }
    }

    fun deleteQuery() {
        if (listDelete.isEmpty()) return
        viewModelScope.launch {
            queryDaoUtil.deleteQuery(listDelete.toTypedArray())
        }
        listDelete.clear()
    }

    fun deleteAll() {
        viewModelScope.launch {
            queryDaoUtil.deleteAllQuery()
        }
    }

    fun updateDateByStr(index: Int) {
        val historyQuery = _queryListLiveData.value?.get(index) ?: return
        viewModelScope.launch {
            historyQuery.time = System.currentTimeMillis()
            queryDaoUtil.updateDateByStr(historyQuery)
        }
    }

    fun getAllQuery() : LiveData<List<HistoryQuery>>{
        return queryDaoUtil.getAllQuery()
    }


}