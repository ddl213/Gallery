package com.example.pagergallery.fragment.me.info

import android.app.Application
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentMeInfoBinding
import com.example.pagergallery.fragment.me.MineViewModel
import com.example.pagergallery.unit.base.fragment.BaseBindFragment
import com.example.pagergallery.unit.base.viewmodel.MyViewModelFactory
import com.example.pagergallery.unit.enmu.InfoFromEnum
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.util.LogUtil


class InfoFragment : BaseBindFragment<FragmentMeInfoBinding>(FragmentMeInfoBinding::inflate), View.OnClickListener {

    private val viewModel by activityViewModels<MineViewModel>{
        MyViewModelFactory(requireContext().applicationContext as Application, MineViewModel::class.java)
    }

    override fun initView() {
        viewModel.setTitle("个人信息")
        binding.apply {
            viewModel.user.value!!.apply {
                tvAccount.text = account.toString()
                tvNameInfo.text = name
                tvPhoneInfo.text = phone.toString()
                tvSexInfo.text = sex
                tvBirthdayInfo.text = birthday
                if (picture.isNullOrBlank()) {
                    ivPhotoInfo.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.boy, null))
                }
                else{
                    ivPhotoInfo.setImageBitmap(BitmapFactory.decodeFile(picture))
                }
            }

        }
    }

    override fun initData() {}

    override fun initEvent() {
        binding.tvNameInfo.setOnClickListener(this)
        binding.tvPhoneInfo.setOnClickListener(this)
        binding.ivPhotoInfo.setOnClickListener(this)
    }

    //设置头像
    private val selectPicture: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 0) return@registerForActivityResult
        viewModel.updatePicture(it.data?.data ?: Uri.EMPTY).apply {
            if (this != null) {
                LogUtil.d("updatePicture1:${this}")
                binding.ivPhotoInfo.setImageBitmap(BitmapFactory.decodeFile(this))
            } else {
                LogUtil.d("updatePicture2:${it.data?.data}")
                requireContext().shortToast("更新失败")
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_name_info->{
                navigate(binding.tvNameInfo.text.toString())
            }
            R.id.tv_phone_info->{
                navigate(binding.tvPhoneInfo.text.toString())
            }
            R.id.iv_photo_info -> {
                //更改头像
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                selectPicture.launch(intent)
            }
        }
    }
    private fun navigate(content : String){
        Bundle().apply {
            putString("content",content)
            putSerializable("from", InfoFromEnum.Name)
            findNavController().navigate(R.id.action_infoFragment_to_resetInfoFragment,this)
        }
    }
}