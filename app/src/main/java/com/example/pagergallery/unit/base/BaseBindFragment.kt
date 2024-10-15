package com.example.pagergallery.unit.base

import android.app.Application
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding

abstract class BaseBindFragment<V : ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> V) : Fragment(){

    private var _binding : V? = null//私有的binding用于获取传进来的binding
    protected val binding get() = _binding!!//只读的binding，用于暴露出去

    private val viewModel by activityViewModels<BaseViewModel>{
        MyViewModelFactory(requireContext(), BaseViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //接收传递的binding
        _binding = inflate(inflater,container,false)
        //由于_binding是可变的，所以使用binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initEvent()
    }

    abstract fun initView()
    abstract fun initData()
    abstract fun initEvent()

    //设置标题栏可见性
    fun setTopAppBarVisible(visible : Boolean, canBack : Boolean, color : Int?, title: String = ""){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(!visible)
        }else{
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window,!visible)
        }

        requireActivity().window.statusBarColor = if (color != null) resources.getColor(
            color,
            null
        ) else Color.TRANSPARENT

        //viewModel.setTopAppBarVisible(visible)
        //viewModel.canBack.value = canBack
    }

    //设置标题栏文字
    fun setTopAppBarTitle(title : String){
    }

    //将binding置为空,防止内存消耗
    override fun onDestroyView() {
        super.onDestroyView()
        //置空
        _binding = null
    }


}