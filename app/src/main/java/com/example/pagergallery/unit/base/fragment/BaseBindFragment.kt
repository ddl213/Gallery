package com.example.pagergallery.unit.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.example.pagergallery.unit.base.viewmodel.BaseViewModel
import com.example.pagergallery.unit.base.viewmodel.MyViewModelFactory

abstract class BaseBindFragment<V : ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> V) :
    Fragment() {

    private var isRootViewInit = false
    private var _binding: V? = null//私有的binding用于获取传进来的binding
    protected val binding get() = _binding!!//只读的binding，用于暴露出去

    private val viewModel by activityViewModels<BaseViewModel> {
        MyViewModelFactory(requireContext(), BaseViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //接收传递的binding
        _binding = inflate(inflater, container, false)
        //由于_binding是可变的，所以使用binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()

    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    abstract fun initView()
    abstract fun initData()
    abstract fun initEvent()

    //将binding置为空,防止内存消耗
    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        //置空
        _binding = null
    }
}