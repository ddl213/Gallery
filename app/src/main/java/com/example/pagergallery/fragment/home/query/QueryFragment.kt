package com.example.pagergallery.fragment.home.query

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentQueryBinding
import com.example.pagergallery.unit.base.fragment.BaseBindFragment
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.showSoftInput
import com.example.pagergallery.unit.view.ExpandableFlowLayout

class QueryFragment : BaseBindFragment<FragmentQueryBinding>(FragmentQueryBinding::inflate),
    OnClickListener, OnEditorActionListener {

    private val viewModel by activityViewModels<QueryViewModel>()
    private var isDelete = false

    override fun initView() {
        binding.apply {
            expandView.setOnClickListener(object : ExpandableFlowLayout.OnClickListener {
                override fun click(index: Int, content: String) {
                    if (isDelete) {
                        viewModel.setDelete(index)
                    } else {
                        viewModel.updateDateByStr(index)
                        navigateWithArgs(content)
                    }
                }
            })
        }

        showSoftInput()
    }

    override fun initData() {
        viewModel.getAllQuery().observe(viewLifecycleOwner){
            viewModel.setQueryList(it)
            binding.expandView.setData(it ?: listOf())
        }
    }

    override fun initEvent() {
        binding.layoutSearch.tvClose.setOnClickListener(this)

        binding.imgDelete.setOnClickListener(this@QueryFragment)
        binding.layoutSearch.tvSearch.setOnClickListener(this@QueryFragment)
        binding.layoutSearch.etSearch.setOnEditorActionListener(this@QueryFragment)
    }

    override fun onResume() {
        super.onResume()
        val text = viewModel.getQuery()
        if (text.isNullOrEmpty().not()){
            binding.layoutSearch.etSearch.setText(text)
        }
    }

    //回车查询
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND ||
            (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
        ) {
            return when (event?.action) {
                KeyEvent.ACTION_UP -> {
                    search()
                    true
                }

                else -> true
            }
        }
        return false
    }

    //点击查询
    private fun search() {
        binding.layoutSearch.etSearch.text.toString().trim().apply text@{
            if (this.isEmpty()){
                requireContext().shortToast("请输入图片描述进行搜索")
                return
            }
            viewModel.insertQuery(this@text)
            navigateWithArgs(this)
        }
    }

    fun navigateWithArgs(query: String) {
        viewModel.setQuery(query)
        Bundle().apply {
            putString("QUERY_TEXT", query)
            findNavController().navigate(R.id.action_queryFragment_to_showQueryFragment, this)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvClose -> {
                findNavController().popBackStack()
            }

            R.id.tvSearch -> {
                search()
            }

            R.id.imgDelete -> {
                isDelete = !isDelete
                if (!isDelete) viewModel.deleteQuery()
                binding.expandView.delete()
            }
        }
    }

    //弹出键盘
    private fun showSoftInput() {
        binding.layoutSearch.etSearch.requestFocus()
        requireContext().showSoftInput(binding.layoutSearch.etSearch)
    }

    override fun onStop() {
        super.onStop()
        viewModel.deleteQuery()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setQuery("")
    }

}