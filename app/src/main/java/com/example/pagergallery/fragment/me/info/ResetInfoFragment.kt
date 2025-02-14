package com.example.pagergallery.fragment.me.info

import android.app.Application
import android.os.Build
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pagergallery.databinding.FragmentInfoResetBinding
import com.example.pagergallery.fragment.me.MineViewModel
import com.example.pagergallery.unit.base.fragment.BaseBindFragment
import com.example.pagergallery.unit.base.viewmodel.MyViewModelFactory
import com.example.pagergallery.unit.enmu.InfoFromEnum
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.util.LogUtil

class ResetInfoFragment :
    BaseBindFragment<FragmentInfoResetBinding>(FragmentInfoResetBinding::inflate) {

    private val viewModel by activityViewModels<MineViewModel> {
        MyViewModelFactory(
            requireContext().applicationContext as Application,
            MineViewModel::class.java
        )
    }


    @Suppress("DEPRECATION")
    override fun initView() {

        viewModel.setTitle("修改信息")
        val content = arguments?.getString("content")
        binding.editTextWithClear.setText(content)

        val isName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("from", InfoFromEnum::class.java) == InfoFromEnum.Name
        } else {
            arguments?.getSerializable("from") as InfoFromEnum? == InfoFromEnum.Name
        }

        binding.editTextWithClear.isMultiLine(false)

        if (!isName) { binding.editTextWithClear.setMaxLength(11) }
        binding.btnSave.setOnClickListener {
            if (binding.editTextWithClear.text?.equals(content) != true) {
                save(isName)
            }
        }
    }

    private fun save(isName: Boolean) {
        if (isName) {
            viewModel.updateName(binding.editTextWithClear.text.toString())
        } else {
            val phone = binding.editTextWithClear.text.toString()
            if (!phone.isDigitsOnly() || phone.length != 11) {
                requireContext().shortToast("请输入正确的手机号").show()
                return
            }
            viewModel.updatePhone(phone.toLong())
        }
        requireContext().shortToast("修改成功").show()
        findNavController().popBackStack()
    }

    override fun initData() {}

    override fun initEvent() {}
}