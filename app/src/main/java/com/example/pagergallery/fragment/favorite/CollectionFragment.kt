package com.example.pagergallery.fragment.favorite

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentCollectionBinding
import com.example.pagergallery.databinding.ImageCellBinding
import com.example.pagergallery.fragment.mine.download.ITEM_TYPE
import com.example.pagergallery.fragment.mine.download.PHOTO_LIST
import com.example.pagergallery.fragment.mine.download.POSITION
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.base.BaseBindFragment
import com.example.pagergallery.unit.base.BaseViewHolder
import com.example.pagergallery.unit.base.adapterOf
import com.example.pagergallery.unit.enmu.FragmentFromEnum
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.loadImage

class CollectionFragment : BaseBindFragment<FragmentCollectionBinding>(FragmentCollectionBinding::inflate) {
    private val viewModel by viewModels<CollectViewModel>()

    override fun initView() {
        viewModel.setTitle("收藏")
        binding.collectRecyclerView.layoutManager = GridLayoutManager(requireContext(),4)
    }

    override fun initData() {
        viewModel.getCollect()
        launchAndRepeatLifecycle(Lifecycle.State.STARTED) {
            viewModel.collectListLive.collect {
                setData(it)
            }
        }
    }
    private fun setData(list: List<Item>){
        val mAdapter = adapterOf(list,ImageCellBinding::class.java,
            initViewHolder){ h, _, item ->
            h.itemView.context.loadImage(item.webFormatURL,h.binding.imgWebUrl)
        }
        binding.collectRecyclerView.adapter = mAdapter
    }
    private val initViewHolder : (BaseViewHolder<ImageCellBinding>) -> Unit = {
        it.itemView.setOnClickListener {  _ ->
            Bundle().apply {
                putParcelableArrayList(
                    PHOTO_LIST,
                    ArrayList(viewModel.collectListLive.value)
                )
                putSerializable(ITEM_TYPE, FragmentFromEnum.Collect)
                putInt(POSITION,it.absoluteAdapterPosition )
                findNavController().navigate(
                    R.id.action_collectionFragment_to_largeViewFragment,
                    this
                )
            }
        }
    }

    override fun initEvent() {}



}