package com.example.pagergallery.fragment.me.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.pagergallery.R
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.view.RegisterCompose

class RegisterFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel> ()

    private val isReset by lazy {
        arguments?.getString(NAVIGATE_TO).let {
            !it.isNullOrEmpty() && it == "Reset"
        }
    }
    private val value = mutableStateOf(-1L)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setTitle(if (isReset) "注册" else "修改密码")
        return ComposeView(requireContext()).apply {
            setContent {
                RegisterCompose(
                    value.value,
                    isReset,
                    dialogClick
                ) { phone, pwd, confirmPwd, account ->
                    click(phone, pwd, confirmPwd, account)
                }
            }
        }
    }

    private fun click(phone: String, pwd: String, confirmPwd: String, account: String?) {
        if (!(phone.isNotEmpty() && phone.isDigitsOnly()) || phone.length != 11) {
            requireContext().shortToast("请输入正确的手机号码").show()
            return
        }
        if (!(pwd.isNotEmpty() || confirmPwd.isNotEmpty())) {
            requireContext().shortToast("密码不能为空").show()
            return
        }
        if (pwd.length < 6) {
            requireContext().shortToast("密码不能小于6位").show()
            return
        }
        if (pwd.replace("\\s+".toRegex(), "") != confirmPwd.replace("\\s+".toRegex(), "")) {
            requireContext().shortToast("两次密码不一致").show()
            return
        }
        if (isReset) {
            reset(pwd, account)
        } else {
            launchAndRepeatLifecycle(Lifecycle.State.RESUMED) {
                viewModel.register(pwd, phone.toLong()).let {
                    if (it != null) {
                        value.value = it
                    } else {
                        requireContext().shortToast("手机号码重复，注册失败").show()
                    }
                }
            }

        }
    }

    private fun reset(pwd: String, account: String?) {
        if ((account.isNullOrEmpty() || !account.isDigitsOnly())) {
            requireContext().shortToast("请输入正确的账号").show()
            return
        }
        launchAndRepeatLifecycle(Lifecycle.State.RESUMED) {
            if (viewModel.resetPwd(account.toLong(), pwd)) {
                requireContext().shortToast("修改成功").show()
                findNavController().popBackStack()
            }
        }
    }

    private val dialogClick: (isBack: Boolean) -> Unit
        get() = {
            if (it) {
                findNavController().popBackStack()
                Bundle().apply {
                    putLong("account", value.value)
                    findNavController().navigate(R.id.loginFragment, this)
                }

            }
        }
}