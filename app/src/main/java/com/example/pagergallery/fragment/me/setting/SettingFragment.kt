package com.example.pagergallery.fragment.me.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pagergallery.fragment.me.MineViewModel
import com.example.pagergallery.unit.view.SettingCompose
import com.example.pagergallery.unit.view.SettingSelectEnum

class SettingFragment : Fragment() {
    private val viewModel by activityViewModels<MineViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setTitle("设置")

        return ComposeView(requireContext()).apply {
            setContent {
                SettingCompose{
                    click(it)
                }
            }
        }
    }

    private fun click(select : SettingSelectEnum){
        when(select){
            SettingSelectEnum.Clear ->{
                viewModel.clearCache()
            }
            SettingSelectEnum.Exit ->{
                viewModel.exit()
                findNavController().popBackStack()
            }
        }
    }

}