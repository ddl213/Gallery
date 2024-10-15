package com.example.pagergallery.fragment.mine.download

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentCollectionBinding
import com.example.pagergallery.databinding.ImageCellBinding
import com.example.pagergallery.fragment.mine.LARGE_VIEW_FROM
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

    @Suppress("DEPRECATION")
    private fun getViewAt() : FragmentFromEnum {
        val at = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(LARGE_VIEW_FROM,FragmentFromEnum::class.java)
        }else{
            arguments?.getSerializable(LARGE_VIEW_FROM) as FragmentFromEnum
        }
        if (at == null) {
            return FragmentFromEnum.Error
        }
        return at
    }

    override fun initView() {
        binding.collectRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
    }

    override fun initData() {
        when(isDownLoad){
            FragmentFromEnum.DownLoad ->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    viewModel.getFilePath(requireActivity())
                }
                launchAndRepeatLifecycle(Lifecycle.State.STARTED){
                    viewModel.downLoadViewList.collect{
                        setData(it)
                    }
                }
                setTopBarInfo("下载")
            }
            FragmentFromEnum.Collect ->{
                setTopBarInfo("收藏")
                viewModel.getCollect()
                launchAndRepeatLifecycle(Lifecycle.State.STARTED) {
                    viewModel.collectListLive.collect {
                        setData(it)
                    }
                }
            }
            FragmentFromEnum.History ->{
                setTopBarInfo("历史记录")
                viewModel.getCaches()
                launchAndRepeatLifecycle(Lifecycle.State.STARTED){
                    viewModel.cacheList.collect{
                        setData(it)
                    }
                }
            }
            else ->{
                requireContext().shortToast("未知错误").show()
                findNavController().popBackStack()
            }
        }
    }

    private fun setTopBarInfo(title : String){
        viewModel.setTitle(title)
    }

    private fun setData(list: List<Any>) {
        val mAdapter = adapterOf(
            list, ImageCellBinding::class.java,
            initViewHolder
        ) { h, _, item ->
            if (item is String) h.itemView.context.loadImage(item, h.binding.imgWebUrl)
            else if (item is Item) h.itemView.context.loadImage(item.webFormatURL, h.binding.imgWebUrl)
        }
        binding.collectRecyclerView.adapter = mAdapter
    }

    private val initViewHolder: (BaseViewHolder<ImageCellBinding>) -> Unit = {
        it.itemView.setOnClickListener { _ ->
            setBundle(it.absoluteAdapterPosition)
        }
    }
    private fun setBundle(pos : Int){
        val bundle = Bundle()
        when(isDownLoad){
            FragmentFromEnum.DownLoad ->{
                bundle.putStringArrayList(PHOTO_LIST, ArrayList(viewModel.downLoadViewList.value))
            }
            FragmentFromEnum.Collect ->{
                bundle.putSerializable(PHOTO_LIST, ArrayList(viewModel.collectListLive.value))
            }
            FragmentFromEnum.History ->{
                bundle.putSerializable(PHOTO_LIST, ArrayList(viewModel.cacheList.value))
            }
            else -> {}
        }
        bundle.putSerializable(ITEM_TYPE,isDownLoad)
        bundle.putInt(POSITION, pos)
        findNavController().navigate(
                R.id.action_downLoadFragment_to_largeViewFragment,
                bundle
            )
    }

    override fun initEvent() {

    }


}