package com.example.pagergallery.fragment.home.query

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentQueryBinding
import com.example.pagergallery.unit.base.BaseBindFragment
import com.example.pagergallery.repository.local.tables.query.HistoryQuery
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.showSoftInput
import com.example.pagergallery.unit.view.ExpandableFlowLayout
import kotlinx.coroutines.launch

class QueryFragment : BaseBindFragment<FragmentQueryBinding>(FragmentQueryBinding::inflate),
    OnClickListener, OnEditorActionListener {

    private val viewModel by activityViewModels<QueryViewModel>()
    private var isDelete = false

    override fun initView() {
        viewModel.setTopAppBarVisible(requireActivity(),false, canBack = true, color = null)
        binding.apply {
            expandView.setOnClickListener(object : ExpandableFlowLayout.OnClickListener {
                override fun click(index: Int, content: String) {
                    if (isDelete){
                        viewModel.setDelete(index)
                    }else {
                        lifecycleScope.launch { viewModel.updateDateByStr(index) }
                        navigateWithArgs(content)
                    }
                }
            })
        }
    }

    override fun initData() {
        viewModel.queryListLiveData.observe(viewLifecycleOwner,dataObserver)
    }

    override fun initEvent() {
        binding.layoutSearch.tvClose.setOnClickListener(this)
        showSoftInput()

        binding.imgDelete.setOnClickListener(this@QueryFragment)
        binding.layoutSearch.tvSearch.setOnClickListener(this@QueryFragment)
        binding.layoutSearch.etSearch.setOnEditorActionListener(this@QueryFragment)
    }

    private val dataObserver = Observer<List<HistoryQuery>?>{list ->
        viewModel.setListItems(list)
        list?.forEach { item ->
            logD("${item.id} -- ${item.queryStr} -- ${item.time}")
        }
        list?.map {
            it.queryStr
        }.also { binding.expandView.setData(it ?: listOf()) }

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
            lifecycleScope.launch { viewModel.insertQuery(this@text) }
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
                if (!isDelete) lifecycleScope.launch { viewModel.deleteQuery() }
                binding.expandView.delete()
            }
        }
    }

    //弹出键盘
    private fun showSoftInput() {
        binding.layoutSearch.etSearch.requestFocus()
        requireContext().showSoftInput(binding.layoutSearch.etSearch)
    }

    override fun onDestroy() {
        viewModel.queryListLiveData.removeObserver(dataObserver)
        super.onDestroy()
    }

}