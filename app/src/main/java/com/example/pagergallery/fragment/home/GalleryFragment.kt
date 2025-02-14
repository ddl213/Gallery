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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentGalleryBinding
import com.example.pagergallery.fragment.me.download.ITEM_TYPE
import com.example.pagergallery.fragment.me.download.PHOTO_LIST
import com.example.pagergallery.fragment.me.download.POSITION
import com.example.pagergallery.unit.enmu.FragmentFromEnum
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.util.KeyValueUtils
import com.example.pagergallery.unit.util.LogUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GalleryFragment(private val isQuery: Boolean, private val type: String) :
    LazyLoadFragment<FragmentGalleryBinding>(
        FragmentGalleryBinding::inflate
    ) {
    private var isFirstLoad = true//在搜索页是否第一次加载

    //用户搜索图片的字段
    private var isResetAdapter = false
    private val viewModel by activityViewModels<GalleryViewModel>()
    private val listener = object : (CombinedLoadStates) -> Unit {
        override fun invoke(p1: CombinedLoadStates) {
            when (p1.refresh) {
                //初次和下拉加载时显示刷新状态
                is LoadState.Loading -> {
                    LogUtil.d("loading")
                }
                //加载完成
                is LoadState.NotLoading -> {
                    //LogUtil.d("LoadState.NotLoading:${viewModel.galleryListLiveData.value?.size} --${count}")
                    setAdapter(true)
                }
                //发生错误时
                is LoadState.Error -> {
//                    LogUtil.d("LoadState.Error:${viewModel.galleryListLiveData.value?.size} --${count}")
                    LogUtil.d("LoadState.Error:${(p1.refresh as LoadState.Error).error.message}")
                    setAdapter(false)
                }
            }
        }
    }
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
                if (viewModel.reLoadState.value && viewModel.getNewItemList(type)
                        ?.isNotEmpty() == true
                ) {
                    mAdapter.submitData(
                        PagingData.from(viewModel.getNewItemList(type)!!)
                    )
                }
            }
        }

        concatAdapter = mAdapter.withLoadStateFooter(loadMoreAdapter)
        mAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.recyclerviewGallery.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(20) // 增加视图缓存
            itemAnimator = null // 必须禁用动画
                layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                    isItemPrefetchEnabled = true
                    invalidateSpanAssignments()
                }
            adapter = concatAdapter
            viewModel.recyclerViewState.value?.let { (layoutManager as StaggeredGridLayoutManager).onRestoreInstanceState(it) }
        }

    }

    override fun initEvent() {
        //设置加载刷新状态
        mAdapter.addLoadStateListener(listener)
        binding.swipeRefreshLayout.setOnRefreshListener { getData() }
    }

    override fun initData() {
        if (isQuery) {
            if (isFirstLoad) {
                getData()
            }
        } else {
            if (viewModel.getNewItemList(type).isNullOrEmpty()) {
                getData()
            }
        }

    }

    //获取图片数据
    private fun getData() {
        binding.swipeRefreshLayout.isRefreshing = true
        isFirstLoad = false
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
    }

    private fun resetAdapter() {
        if (mAdapter.itemCount <= 0) return
        if (isResetAdapter) {
            binding.recyclerviewGallery.scrollToPosition(0)
            isResetAdapter = false
        } else {
            //if (binding.recyclerviewGallery.scto)
        }
    }

    override fun onResume() {
        super.onResume()
        restorePosition()
    }

    private fun restorePosition(){
        val position = KeyValueUtils.getInt("SCROLL_POSITION")

        if (position < 2) return
        binding.recyclerviewGallery.post {
            binding.recyclerviewGallery.apply {
                val lm = layoutManager as? StaggeredGridLayoutManager

                val firstPosition =lm?.findFirstVisibleItemPositions(null)?.minOrNull() ?: 0
                val lastPosition = lm?.findLastVisibleItemPositions(null)?.maxOrNull() ?: 0

                if (position in firstPosition .. lastPosition) return@apply
                lm?.scrollToPosition(position)
            }
        }

        KeyValueUtils.setInt("SCROLL_POSITION",0)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.swipeRefreshLayout.isRefreshing = false
        mAdapter.removeLoadStateListener(listener)
        viewModel.recyclerViewState.value = binding.recyclerviewGallery.layoutManager?.onSaveInstanceState()
    }

    override fun onDestroy() {
        viewModel.reLoadState.value = true
        super.onDestroy()
        LogUtil.d("销毁")
    }
}
