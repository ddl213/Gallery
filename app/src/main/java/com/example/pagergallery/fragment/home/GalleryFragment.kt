package com.example.pagergallery.fragment.home

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentGalleryBinding
import com.example.pagergallery.fragment.me.download.ITEM_TYPE
import com.example.pagergallery.fragment.me.download.PHOTO_LIST
import com.example.pagergallery.fragment.me.download.POSITION
import com.example.pagergallery.unit.enmu.FragmentFromEnum
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.logD
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GalleryFragment(private val isQuery: Boolean, private val type: String) :
    LazyLoadFragment<FragmentGalleryBinding>(
        FragmentGalleryBinding::inflate
    ) {
    //用户搜索图片的字段
    private var isResetAdapter = false
    private var count = 0
    private val viewModel by activityViewModels<GalleryViewModel>()
    private val mAdapter by lazy {
        GalleryAdapter {
            Bundle().apply {
                putParcelableArrayList(
                    PHOTO_LIST,
                    ArrayList(viewModel.getNewItemList(type) ?: listOf())
                )
                putSerializable(ITEM_TYPE, FragmentFromEnum.Gallery)
                putInt(POSITION, it ?: 0)
                findNavController().navigate(
                    if (!isQuery) R.id.action_baseFragment_to_largeViewFragment
                    else R.id.action_showQueryFragment_to_largeViewFragment,
                    this
                )
            }
        }
    }

    //组合adapter
    private lateinit var concatAdapter: ConcatAdapter
    private val loadMoreAdapter by lazy {
        LoadMoreAdapter { mAdapter.retry() }
    }
    private val emptyAdapter = EmptyAdapter { mAdapter.retry() }

    /**
     * 各个控件的功能设置
     */
    override fun initView() {
        if (!isQuery) {
            launchAndRepeatLifecycle {
                if (viewModel.reLoadState.value && viewModel.getNewItemList(type)?.isNotEmpty() == true
                ) {
                    //if (mAdapter.itemCount != 0) return@launchAndRepeatLifecycle
                    mAdapter.submitData(
                        PagingData.from(viewModel.getNewItemList(type)!!)
                    )
                }
            }
        }
        concatAdapter = mAdapter.withLoadStateFooter(loadMoreAdapter)
        binding.apply {
            recyclerviewGallery.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = concatAdapter
            }
        }
    }

    override fun initEvent() {
        //设置加载刷新状态
        mAdapter.addLoadStateListener(listener)
        binding.swipeRefreshLayout.setOnRefreshListener { getData() }

    }

    private var isFirstLoad = true
    override fun initData() {
        if ((viewModel.getNewItemList(type)?.isEmpty() == true) && !isQuery ||
            (isQuery && isFirstLoad)
        ) {
            binding.swipeRefreshLayout.isRefreshing = true
            getData()
            isFirstLoad = false
        }
    }

    //获取api数据
    private fun getData() {
        lifecycleScope.launch {
            isResetAdapter = true
            viewModel.getData().collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

    private fun setAdapter(isEmpty: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = false

        //没有数据时，如果装有底部adapter就移除，如果没有空视图就添加一个
        if (mAdapter.itemCount == 0) {
            if (concatAdapter.adapters.contains(loadMoreAdapter)) concatAdapter.removeAdapter(
                loadMoreAdapter
            )
            if (!concatAdapter.adapters.contains(emptyAdapter)) {
                emptyAdapter.setText(isEmpty)
                concatAdapter.addAdapter(emptyAdapter)
            }
            Toast.makeText(requireContext(), "没有找到相关结果~", Toast.LENGTH_SHORT).show()

        } else {
            //有数据时如果添加了空视图就移除，添加底部adapter
            if (concatAdapter.adapters.contains(emptyAdapter)) {
                concatAdapter.removeAdapter(emptyAdapter)
                concatAdapter.addAdapter(loadMoreAdapter)
            }
        }
        resetAdapter()
        count++
    }
    private fun resetAdapter() {
        if (isResetAdapter && mAdapter.itemCount > 0) {
            binding.recyclerviewGallery.scrollToPosition(0)
            isResetAdapter = false
        }
    }

    private val listener = object : (CombinedLoadStates) -> Unit {
        override fun invoke(p1: CombinedLoadStates) {
            when (p1.refresh) {
                //初次和下拉加载时显示刷新状态
                is LoadState.Loading -> {
                    logD("loading")
                }
                //加载完成
                is LoadState.NotLoading -> {
                    //logD("LoadState.NotLoading:${viewModel.galleryListLiveData.value?.size} --${count}")
                    setAdapter(true)
                }
                //发生错误时
                is LoadState.Error -> {
//                    logD("LoadState.Error:${viewModel.galleryListLiveData.value?.size} --${count}")
//                    logD("LoadState.Error:${(p1.refresh as LoadState.Error).error.message}")
                    setAdapter(false)
                }
            }
        }

    }

    override fun onDestroyView() {
        binding.swipeRefreshLayout.isRefreshing = false
        mAdapter.removeLoadStateListener(listener)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.reLoadState.value = true
    }
}
