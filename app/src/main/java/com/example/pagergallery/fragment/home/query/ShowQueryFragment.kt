package com.example.pagergallery.fragment.home.query

import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentShowQueryBinding
import com.example.pagergallery.fragment.home.GalleryFragment
import com.example.pagergallery.fragment.home.GalleryViewModel
import com.example.pagergallery.fragment.home.list_type
import com.example.pagergallery.unit.base.fragment.BaseBindFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class ShowQueryFragment : BaseBindFragment<FragmentShowQueryBinding>(FragmentShowQueryBinding::inflate) ,
    OnClickListener,OnTabSelectedListener {

    private val viewModel by activityViewModels<GalleryViewModel>()
    private var currentTab = 0

    override fun initView() {
        binding.imgSearch.visibility = View.GONE
        currentTab = viewModel.currentTab.value
        binding.viewpager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4
            override fun createFragment(position: Int): Fragment {
                return GalleryFragment(true, list_type[position])
            }
        }

        TabLayoutMediator(binding.tabType,binding.viewpager2) { tab, position ->
            when(position){
                0 -> tab.text = "推荐"
                1 -> tab.text = "照片"
                2 -> tab.text = "插画"
                3 -> tab.text = "矢量图"
            }
        }.attach()

        binding.layoutSearch.etSearch.setText(arguments?.getString("QUERY_TEXT"))
    }

    override fun initData() {
    }

    override fun initEvent() {
        binding.tabType.addOnTabSelectedListener(this)
        binding.layoutSearch.tvClose.setOnClickListener(this)

        binding.layoutSearch.tvSearch.setOnClickListener(this)
        binding.layoutSearch.etSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN){
                findNavController().popBackStack()
            }

            return@setOnTouchListener true
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.position.apply {
            viewModel.currentTab.value = this ?: 0
            viewModel.setImageTypeStr()
        }
    }
    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvClose ,
            R.id.tvSearch->{
                findNavController().popBackStack()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearQueryList()
        viewModel.currentTab.value = currentTab
    }
}