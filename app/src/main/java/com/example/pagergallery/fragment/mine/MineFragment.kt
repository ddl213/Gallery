package com.example.pagergallery.fragment.mine

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentMineBinding
import com.example.pagergallery.repository.local.tables.user.User
import com.example.pagergallery.unit.base.BaseBindFragment
import com.example.pagergallery.unit.enmu.FragmentFromEnum
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.shortToast

const val LARGE_VIEW_FROM = "large_view_from"
class MineFragment : BaseBindFragment<FragmentMineBinding>(FragmentMineBinding::inflate),
    OnClickListener {
    private val viewModel by activityViewModels<MineViewModel> ()

    override fun initView() {

        if (!viewModel.getLoginState() ) {
            notLogIn()
        } else {
            isLogIn()
        }
    }

    private fun isLogIn() {
        launchAndRepeatLifecycle(Lifecycle.State.STARTED) {
            viewModel.user.collect{
                if (it != null) initInfo(it)
            }
        }
    }

    private fun initInfo(user : User){
        binding.apply {
            if (user.picture.isNullOrEmpty()) {
                logD("mineFragment:user.picture.isNullOrEmpty()")
                photo.setImageDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.boy, null)
                )
            } else {
                logD("mineFragment:setImageBitmap")
                photo.setImageBitmap(BitmapFactory.decodeFile(user.picture))
            }
            tvName.text = user.name
            tvAccountID.text = user.account.toString()
        }
    }

    private fun notLogIn() {
        binding.mineConstraintLayout.visibility = View.GONE
        binding.loginButtonCompose.apply {
            visibility = View.VISIBLE
            setContent {
                LoginButtonCompose {
                    findNavController().navigate(R.id.action_mineFragment_to_loginFragment)
                }
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        binding.apply {
            mineConstraintLayout.setOnClickListener(this@MineFragment)
            tvDownload.setOnClickListener(this@MineFragment)
            tvCollect.setOnClickListener(this@MineFragment)
            tvHistory.setOnClickListener(this@MineFragment)
            tvSetting.setOnClickListener(this@MineFragment)
            photo.setOnClickListener(this@MineFragment)
        }
    }

    //设置头像
    private val selectPicture: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 0) return@registerForActivityResult
        viewModel.updatePicture(it.data?.data ?: Uri.EMPTY).apply {
            if (this != null) {
                logD("mineFragment:selectPicture $this")
                binding.photo.setImageURI(it.data?.data)
            } else {
                logD("mineFragment:selectPicture fail")
                requireContext().shortToast("更新失败")
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.mineConstraintLayout -> {
                findNavController().navigate(R.id.action_mineFragment_to_infoFragment)
            }

            binding.photo -> {
                //更改头像
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                selectPicture.launch(intent)
            }

            binding.tvSetting -> {
                findNavController().navigate(R.id.action_mineFragment_to_settingFragment)
            }

            binding.tvDownload -> {
                Bundle().apply {
                    putSerializable(LARGE_VIEW_FROM,FragmentFromEnum.DownLoad)
                    findNavController().navigate(R.id.action_mineFragment_to_downLoadFragment,this)
                }

            }

            binding.tvCollect -> {
                Bundle().apply {
                    putSerializable(LARGE_VIEW_FROM,FragmentFromEnum.Collect)
                    findNavController().navigate(R.id.action_mineFragment_to_downLoadFragment,this)
                }
            }

            binding.tvHistory -> {
                Bundle().apply {
                    putSerializable(LARGE_VIEW_FROM,FragmentFromEnum.History)
                    findNavController().navigate(R.id.action_mineFragment_to_downLoadFragment,this)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveUserInfo()
        logD("mineFragment:OnDestroy")
    }

}