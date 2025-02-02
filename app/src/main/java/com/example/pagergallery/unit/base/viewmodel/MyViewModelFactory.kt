package com.example.pagergallery.unit.base.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//创建带参数的viewModel
class MyViewModelFactory<V:ViewModel>(private val context: Context,private val clazz: Class<V>) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return clazz.getConstructor(Context::class.java).newInstance(context) as T
    }
}