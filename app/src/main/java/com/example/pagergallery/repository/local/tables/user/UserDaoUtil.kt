package com.example.pagergallery.repository.local.tables.user

import android.database.sqlite.SQLiteConstraintException
import com.example.pagergallery.unit.logD


class UserDaoUtil(private val userDao: UserDao) {

    //登录
    suspend fun login(account: Long, pwd: String) =
        userDao.login(account, pwd)

    //是否存在该账号
    suspend fun existAccount(account: Long) =
        userDao.existAccount(account)

    //注册
    suspend fun register(account: Long, phone: Long, pwd: String) : Long? {
        return try {
            userDao.addUser(
                User(
                    null,
                    account = account,
                    name = "用户:$account",
                    pwd = pwd,
                    phone = phone
                )
            )
        }catch (sql : SQLiteConstraintException){
            logD("UserDaoUtil fail:register")
            null
        }

    }

    //修改信息
    suspend fun updateUser(user: User) =
        userDao.updateUser(user)

    suspend fun updateUserPwd(pwd: String, account: Long) =
        userDao.updateUserPwd(pwd, account)

    suspend fun updateUserPhone(phone: Long, account: Long) =
        userDao.updateUserPhone(phone, account)

    suspend fun updateUserName(name: String, account: Long) =
        userDao.updateUserName(name, account)

    suspend fun updateUserSex(sex: String, account: Long) =
        userDao.updateUserSex(sex, account)

    suspend fun updateUserBirthday(birthday: String, account: Long) =
        userDao.updateUserBirthday(birthday, account)

    suspend fun updateUserPicture(picture: String, account: Long) =
        userDao.updateUserPicture(picture, account)


    //注销
    suspend fun deleteUser(user: User) =
        userDao.deleteUser(user)

    suspend fun deleteUserByAccount(account: Long) =
        userDao.deleteUserByAccount(account)


}