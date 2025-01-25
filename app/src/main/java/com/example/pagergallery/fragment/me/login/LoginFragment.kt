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
import com.example.pagergallery.unit.enmu.LoginNavigateTo
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.view.LoginCompose

const val NAVIGATE_TO = "navigate"

class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private val loginState = mutableStateOf(false)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setTitle("登录")
        val account = arguments?.getLong("account")
        return ComposeView(requireContext()).apply {
            setContent {
                LoginCompose(loginState.value, account) { navigate, account, pwd ->
                    navigateTo(navigate, account, pwd)
                }
            }
        }
    }

    private fun navigateTo(navigateTo: LoginNavigateTo, account: String?, pwd: String?) {
        when (navigateTo) {
            LoginNavigateTo.Login -> {
                if (account.isNullOrEmpty() || pwd.isNullOrEmpty()) {
                    requireContext().shortToast("账号密码不能为空")
                    return
                }
                launchAndRepeatLifecycle(Lifecycle.State.RESUMED) {
                    if (account.isDigitsOnly()) {
                        val success = viewModel.login(account.toLong(), pwd)
                        if (success) {
                            requireContext().shortToast("登录成功").show()
                            findNavController().popBackStack()
                        } else {
                            requireContext().shortToast("登录失败").show()
                        }
                    } else {
                        requireContext().shortToast("账号只有数字组成，请重新输入").show()
                    }
                }
            }

            LoginNavigateTo.Register, LoginNavigateTo.Reset -> {
                Bundle().apply {
                    putString(NAVIGATE_TO, navigateTo.toString())
                    findNavController().navigate(
                        R.id.action_loginFragment_to_registerFragment,
                        this
                    )
                }
            }

            else -> {
                requireContext().shortToast("该功能未完善，敬请期待！").show()
                viewModel.thirdPartLogin(navigateTo)
            }
        }
    }
}