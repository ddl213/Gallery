package com.example.pagergallery.fragment.home

import android.content.ClipData.Item
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
import com.example.pagergallery.unit.util.IConstStringUtil
import com.example.pagergallery.unit.util.KeyValueUtils
import com.example.pagergallery.unit.util.LogUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GalleryFragment(private val isQuery: Boolean, private val type: String) :
    LazyLoadFragment<FragmentGalleryBinding>(
        FragmentGalleryBinding::inflate
    ) {
    private var isFirstLoad = true//是否第一次加载

    //用户搜索图片的字段
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
                    LogUtil.d("加载完成LoadState.NotLoading:")
                    setAdapter(true)
                }
                //发生错误时
                is LoadState.Error -> {
//                    LogUtil.d("LoadState.Error:${viewModel.galleryListLiveData.value?.size} --${count}")
                    LogUtil.d("发生错误时LoadState.Error:${(p1.refresh as LoadState.Error).error}")
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
    private val emptyAdapter = EmptyAdapter { mAdapter.retry() }
    private val loadMoreAdapter = LoadMoreAdapter { mAdapter.retry() }

    /**
     * 各个控件的功能设置
     */
    override fun initView() {
        concatAdapter = mAdapter.withLoadStateFooter(loadMoreAdapter)
        mAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT

        binding.recyclerviewGallery.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(20) // 增加视图缓存
            itemAnimator = null // 禁用动画
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                    isItemPrefetchEnabled = true
                    invalidateSpanAssignments()
                }
            adapter = concatAdapter
            viewModel.recyclerViewState.value?.let {
                (layoutManager as? StaggeredGridLayoutManager)?.onRestoreInstanceState(
                    it
                )
            }
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
                isFirstLoad = false
            }
        } else {
            LogUtil.d("initData: 是否有数据 ${!viewModel.getNewItemList(type).isNullOrEmpty()}")
            if (viewModel.getNewItemList(type).isNullOrEmpty()) {
                getData()
            }
        }
    }

    //获取图片数据
    private fun getData() {
        binding.swipeRefreshLayout.isRefreshing = true
        lifecycleScope.launch {
            viewModel.getData().collectLatest {
                LogUtil.d("viewModel.getData().collectLatest")
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
        LogUtil.d("setAdapter ")
    }

    /**onResume()*/
    override fun onResume() {
        super.onResume()
        //滚动到指定位置
        if (!viewModel.reLoadState.value) {
            val position = KeyValueUtils.getInt(IConstStringUtil.SCROLL_POSITION)
            if (position != -1) {
                restorePosition(position)
                KeyValueUtils.setInt(IConstStringUtil.SCROLL_POSITION, -1)
            }
        } else {
            viewModel.reLoadState.value = false
        }
    }

    private fun restorePosition(position: Int, visibility: Boolean = false) {
        binding.recyclerviewGallery.postDelayed({
            binding.recyclerviewGallery.apply {
                //在向上滚动到可见位置后，再次滚动到屏幕的顶部
                if (visibility) {
                    if (position >= 0 && position < this.childCount) {
                        val top = this.getChildAt(position)?.top ?: 0
                        if (top == 0) return@apply
                        this.smoothScrollBy(0, top)
                    }
                    return@apply
                }

                //没有做任何滚动操作才执行下面代码
                val lm = (this.layoutManager as? StaggeredGridLayoutManager) ?: return@apply
                val firstPosition =
                    lm.findFirstCompletelyVisibleItemPositions(null)?.minOrNull() ?: 0
                val lastPosition = lm.findLastCompletelyVisibleItemPositions(null)?.maxOrNull() ?: 0

                if (firstPosition == -1 || lastPosition == -1) return@apply

                if (position < firstPosition) {
                    this.smoothScrollToPosition(position)
                } else if (position > lastPosition) {
                    this.scrollToPosition(position)
                    restorePosition(position, true)
                }
            }
        }, 30)
    }

    /**Stop()*/
    override fun onStop() {
        super.onStop()
        onSaveScrollState()
    }

    private fun onSaveScrollState() {
        viewModel.recyclerViewState.value =
            binding.recyclerviewGallery.layoutManager?.onSaveInstanceState()
    }

    /**onDestroyView()*/
    override fun onDestroyView() {
        super.onDestroyView()
        binding.swipeRefreshLayout.isRefreshing = false
        mAdapter.removeLoadStateListener(listener)
    }

    /**onDestroy()*/
    override fun onDestroy() {
        super.onDestroy()
        viewModel.reLoadState.value = true
        val value = viewModel.getNewItemList(type)
        if (!value.isNullOrEmpty()) {
            KeyValueUtils.setString(type, Gson().toJson(value))
        }
    }


    /**onViewStateRestored(savedInstanceState: Bundle?)*/
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (!isQuery) {
            lifecycleScope.launch {

                //如果是第一次打开，就先显示上次加载的数据。再重新加载数据
                if (viewModel.getActivityLoadState() == true) {
                    val json = KeyValueUtils.getString(type)
                    if (!json.isNullOrEmpty()) {
                        val typeToken = object :
                            TypeToken<List<com.example.pagergallery.repository.api.Item>>() {}
                        val value = Gson().fromJson(json, typeToken)
                        mAdapter.submitData(PagingData.from(value))
                    }
                    return@launch
                }
                //不是第一次加载，就将已加载的数据显示出来
                if ((mAdapter.itemCount == 0) && (viewModel.getNewItemList(type)
                        ?.isNotEmpty() == true)
                ) {
                    mAdapter.submitData(
                        PagingData.from(viewModel.getNewItemList(type)!!)
                    )
                }
            }
        }
    }
}
