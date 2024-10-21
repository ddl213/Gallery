package com.example.pagergallery.fragment.mine

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.local.tables.user.User
import kotlinx.coroutines.launch

class MineViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(application)
    private val userDaoUtil = repository.getUserDaoUtil()
    private val cacheDaoUtil = repository.getCacheDaoUtil()
    val user = repository.user

    fun setTitle(title: String){
        repository.setTitle(title)
    }

    fun getLoginState() = repository.getLoginState()

    fun setUserInfo(user: User) {
        repository.setUser(user)
    }

    fun saveUserInfo() {
        repository.saveUserInfo()
    }

    fun exit() {
        repository.setUser(null)
    }

    //更改头像，内存中存储的用户头像也需要更改，已达到同步
    fun updatePicture(uri: Uri): String? {
        val path = getPath(uri).apply picture@{
            viewModelScope.launch {
                if (!this@picture.isNullOrBlank()) {
                    userDaoUtil.updateUserPicture(this@picture, user.value!!.account)
                    repository.user.value!!.apply {
                        picture = this@picture
                        repository.setUser(this)
                    }
                }
            }
        }
        return path
    }

    //获取图片路径
    private fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val path =
            getApplication<Application>().contentResolver.query(uri, projection, null, null, null)
                ?.let {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    it.moveToFirst()
                    val path = it.getString(columnIndex)
                    it.close()
                    path
                }
        return path
    }

    //更改名字
    fun updateName(name: String) {
        viewModelScope.launch {
            repository.user.value!!.apply {
                this.name = name
                repository.setUser(this)
            }
            userDaoUtil.updateUserName(name, user.value!!.account)
        }
    }

    //更改手机号码
    fun updatePhone(phone: Long) {
        viewModelScope.launch {
            repository.user.value!!.apply {
                this.phone = phone
                repository.setUser(this)
            }
            userDaoUtil.updateUserPhone(phone, user.value!!.account)
        }
    }

    //更改性别
    suspend fun updateSex(sex: String): Boolean {
        repository.user.value!!.apply {
            this.sex = sex
            repository.setUser(this)
        }
        return userDaoUtil.updateUserSex(sex, user.value!!.account).run {
            this != -1
        }
    }

    //更改生日
    suspend fun updateBirthday(birthday: String): Boolean {
        repository.user.value!!.apply {
            this.birthday = birthday
            repository.setUser(this)
        }
        return userDaoUtil.updateUserBirthday(birthday, user.value!!.account).run {
            this != -1
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            cacheDaoUtil.deleteAll()
        }
    }


}