package com.example.pagergallery.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.pagergallery.unit.base.fragment.BaseBindFragment

abstract class LazyLoadFragment<V : ViewBinding>(
    inflate : (LayoutInflater,ViewGroup?,Boolean) -> V
) : BaseBindFragment<V>(inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    abstract override fun initView()
    abstract override fun initEvent()
    abstract override fun initData()

}