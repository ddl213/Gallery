package com.example.pagergallery.fragment.mine.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.unit.enmu.LoginNavigateTo
import kotlin.random.Random

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(getApplication())
    private val userDaoUtil = repository.getUserDaoUtil()
    fun setTitle(title: String){
        repository.setTitle(title)
    }

    //登录
    suspend fun login(account: Long, pwd: String): Boolean {
        return userDaoUtil.login(account, pwd).run {
            if (this != null){
                repository.setUser(this)
                true
            }else false
        }
    }

    //注册
    suspend fun register(pwd: String, phone: Long): Long? {
        var random = -1L
        while (random == -1L) {
            Random.nextLong(100000, 1000000000).apply {
                if (!userDaoUtil.existAccount(this)) {
                    random = this
                }
            }
        }
        userDaoUtil.register(random, phone, pwd).also {
            return if (it != null) {
                random
            } else null
        }
    }

    //重置密码
    suspend fun resetPwd(account: Long, pwd: String): Boolean {
        return userDaoUtil.updateUserPwd(pwd, account) != -1
    }

    //第三方登录
    fun thirdPartLogin(navigateTo: LoginNavigateTo) {}

}