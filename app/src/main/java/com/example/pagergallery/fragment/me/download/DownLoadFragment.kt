package com.example.pagergallery.fragment.me.download

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentCollectionBinding
import com.example.pagergallery.databinding.ImageCellBinding
import com.example.pagergallery.fragment.me.LARGE_VIEW_FROM
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.base.BaseBindFragment
import com.example.pagergallery.unit.base.BaseViewHolder
import com.example.pagergallery.unit.base.adapterOf
import com.example.pagergallery.unit.enmu.FragmentFromEnum
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.loadImage
import com.example.pagergallery.unit.shortToast

const val PHOTO_LIST = "photo_list"
const val POSITION = "position"
const val ITEM_TYPE = "item_type"

class DownLoadFragment :
    BaseBindFragment<FragmentCollectionBinding>(FragmentCollectionBinding::inflate) {

    private val viewModel by viewModels<DownLoadViewModel>()
    private val isDownLoad by lazy { getViewAt() }
    private val mAdapter = adapterOf<Item, ImageCellBinding>(
        ImageCellBinding::class.java
    ) { h, _, item ->
//        if (item is String) h.itemView.context.loadImage(item, h.binding.imgWebUrl)
//        else if (item is Item) h.itemView.context.loadImage(
//            item.webFormatURL,
//            h.binding.imgWebUrl
//        )
        h.itemView.context.loadImage(
            item?.webFormatURL,
            h.binding.imgWebUrl
        )
    }


    @Suppress("DEPRECATION")
    private fun getViewAt(): FragmentFromEnum {
        val at = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(LARGE_VIEW_FROM, FragmentFromEnum::class.java)
        } else {
            arguments?.getSerializable(LARGE_VIEW_FROM) as FragmentFromEnum
        }
        if (at == null) {
            return FragmentFromEnum.Error
        }
        return at
    }

    override fun initView() {
        binding.collectRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.collectRecyclerView.adapter = mAdapter
    }

    override fun initData() {
        when (isDownLoad) {
            FragmentFromEnum.DownLoad -> {
                viewModel.getDownload()
                launchAndRepeatLifecycle(Lifecycle.State.STARTED) {
                    viewModel.downLoadViewList.collect {
                        mAdapter.setNewInstance(it.toMutableList())
                    }
                }
                setTopBarInfo("下载")
            }

            FragmentFromEnum.Collect -> {
                setTopBarInfo("收藏")
                viewModel.getCollect()
                launchAndRepeatLifecycle(Lifecycle.State.STARTED) {
                    viewModel.collectListLive.collect {
                        mAdapter.setNewInstance(it.toMutableList())
                    }
                }
            }

            FragmentFromEnum.History -> {
                setTopBarInfo("历史记录")
                viewModel.getCaches()
                launchAndRepeatLifecycle(Lifecycle.State.STARTED) {
                    viewModel.cacheList.collect {
                        mAdapter.setNewInstance(it.toMutableList())
                    }
                }
            }

            else -> {
                requireContext().shortToast("未知错误").show()
                findNavController().popBackStack()
            }
        }
    }

    //设置标题
    private fun setTopBarInfo(title: String) {
        viewModel.setTitle(title)
    }

//    private fun setData(list: List<Any>) {
////        val mAdapter = adapterOf(
////            list, ImageCellBinding::class.java,
////            initViewHolder
////        ) { h, _, item ->
////            if (item is String) h.itemView.context.loadImage(item, h.binding.imgWebUrl)
////            else if (item is Item) h.itemView.context.loadImage(
////                item.webFormatURL,
////                h.binding.imgWebUrl
////            )
////        }
//
//    }

    private val initViewHolder: (BaseViewHolder<ImageCellBinding>) -> Unit
        get() = {
            it.itemView.setOnClickListener { _ ->
                setBundle(it.absoluteAdapterPosition)
            }
        }



    override fun initEvent() {
        mAdapter.setOnItemClickListener { _, _, position ->
            setBundle(position)
        }
    }

    //跳转大图页
    private fun setBundle(pos: Int) {
        val bundle = Bundle()
        when (isDownLoad) {
            FragmentFromEnum.DownLoad -> {
                bundle.putSerializable(PHOTO_LIST, ArrayList(viewModel.downLoadViewList.value))
            }

            FragmentFromEnum.Collect -> {
                bundle.putSerializable(PHOTO_LIST, ArrayList(viewModel.collectListLive.value))
            }

            FragmentFromEnum.History -> {
                bundle.putSerializable(PHOTO_LIST, ArrayList(viewModel.cacheList.value))
            }

            else -> {}
        }
        bundle.putSerializable(ITEM_TYPE, isDownLoad)
        bundle.putInt(POSITION, pos)
        findNavController().navigate(
            R.id.action_downLoadFragment_to_largeViewFragment,
            bundle
        )
    }

}