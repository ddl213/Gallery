package com.example.pagergallery.unit.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.pagergallery.repository.Repository

class BaseViewModel(context: Context) : ViewModel() {
    private val repository = Repository.getInstance(context)

    //设置标题栏可见性
    val canBack = repository.canBack

    //是否能返回
    val visible = repository.visible

    //标题栏信息
    val title = repository.title

    val topBar = repository.color


}