package com.example.pagergallery.fragment.home.query

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.local.tables.query.HistoryQuery
import com.example.pagergallery.repository.local.tables.query.QueryDaoUtil

class QueryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(getApplication())
    private val _queryListLiveData = MutableLiveData<List<HistoryQuery>>()
    private val queryDaoUtil : QueryDaoUtil get() = repository.getQueryDaoUtil()

    private val listDelete = ArrayList<HistoryQuery>()

    fun setTopAppBarVisible(activity: FragmentActivity, visible : Boolean, canBack : Boolean, color : Int?, title: String = ""){
        repository.setTopAppBarVisible(activity, visible, canBack, color, title)
    }

    //记录查询字段
    fun setQuery(str: String) {
        repository.setQuery(str)
    }

    //增删改查
    val queryListLiveData : LiveData<List<HistoryQuery>> get() = queryDaoUtil.getAllQuery()

    suspend fun deleteQuery() {
        queryDaoUtil.deleteQuery(*listDelete.toTypedArray())
    }

    suspend fun deleteAll(){
        queryDaoUtil.deleteAll()
    }

    suspend fun insertQuery(query:String){
        queryDaoUtil.insertQuery(HistoryQuery(null,query,System.currentTimeMillis()))
    }

    suspend fun updateDateByStr(index : Int){
        queryDaoUtil.updateDateByStr(
            HistoryQuery(
                _queryListLiveData.value?.get(index)?.id,
                _queryListLiveData.value?.get(index)?.queryStr ?: "",
                System.currentTimeMillis()
            )
        )
    }




//    val queryListLiveData: LiveData<List<HistoryQuery>> get() = repository.getAllQuery()
//
//    fun deleteQuery() {
//        viewModelScope.launch {
//            repository.deleteQuery(
//                *listDelete.toTypedArray()
////                _queryListLiveData.value?.get(index)
////                    ?: HistoryQuery(_queryListLiveData.value?.get(index)?.id, "", 0L)
//            )
//        }
//    }
//
//    suspend fun deleteAllQuery() {
//        repository.deleteAllQuery()
//    }
//
//    suspend fun insertQuery(query: String) {
//        repository.insertQuery(HistoryQuery(null, query, System.currentTimeMillis()))
//    }
//
//    fun updateDateByStr(index: Int) {
//        viewModelScope.launch {
//            repository.updateDateByStr(
//                HistoryQuery(
//                    _queryListLiveData.value?.get(index)?.id,
//                    _queryListLiveData.value?.get(index)?.queryStr ?: "",
//                    System.currentTimeMillis()
//                )
//            )
//        }
//    }

    fun setListItems(list : List<HistoryQuery>?){
        _queryListLiveData.value = list ?: listOf()
    }

    fun setDelete(index: Int){
        _queryListLiveData.value?.get(index)?.let { listDelete.add(it) }
    }
}