package com.example.pagergallery.fragment.home

import android.graphics.Color
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentBaseBinding
import com.example.pagergallery.unit.base.BaseBindFragment
import com.example.pagergallery.unit.enmu.ImageTypeEnum
import com.example.pagergallery.unit.logD
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator


val list_type = listOf("all","photo","illustration","vector")
class BaseFragment : BaseBindFragment<FragmentBaseBinding>(FragmentBaseBinding::inflate),OnTabSelectedListener,OnClickListener{

    private val viewModel by activityViewModels<GalleryViewModel>()

    override fun initView() {

        binding.layoutTab.root.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.teal_700))
        binding.layoutTab.tabType.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(),R.color.white))

        binding.viewpager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4
            override fun createFragment(position: Int): Fragment {
                return GalleryFragment(false, list_type[position])
            }
        }
        TabLayoutMediator(binding.layoutTab.tabType,binding.viewpager2) { tab, position ->
            when(position){
                0 -> tab.text = "推荐"
                1 -> tab.text = "照片"
                2 -> tab.text = "插画"
                3 -> tab.text = "矢量图"
            }
        }.attach()

    }

    override fun initData() {
        if (viewModel.reLoadState.value) {
            binding.layoutTab.tabType.apply {
                selectTab(getTabAt(viewModel.currentTab.value))
            }
            binding.viewpager2.setCurrentItem(viewModel.currentTab.value,false)
        }
    }
    override fun initEvent() {
        binding.layoutTab.tabType.addOnTabSelectedListener(this)
        binding.layoutTab.imgSearch.setOnClickListener(this)
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
        if (v?.id == R.id.imgSearch){
            findNavController().navigate(R.id.action_baseFragment_to_queryFragment)
        }
    }

}