package com.example.pagergallery.repository.local.tables.user

import androidx.room.Dao
import androidx.room.Query
import com.example.pagergallery.unit.base.dao.BaseDao
import com.example.pagergallery.unit.util.IConstStringUtil

@Dao
interface UserDao : BaseDao<User>{
    @Query("DELETE FROM ${IConstStringUtil.TABLE_USER} WHERE account = :account")
    suspend fun deleteUserByAccount(account : Long) : Int

    @Query("UPDATE ${IConstStringUtil.TABLE_USER} SET name = :name WHERE account = :account")
    suspend fun updateUserName(name : String,account : Long) : Int
    @Query("UPDATE ${IConstStringUtil.TABLE_USER} SET phone = :phone WHERE account = :account")
    suspend fun updateUserPhone(phone : Long,account : Long) : Int
    @Query("UPDATE ${IConstStringUtil.TABLE_USER} SET sex = :sex WHERE account = :account")
    suspend fun updateUserSex(sex : String,account : Long) : Int
    @Query("UPDATE ${IConstStringUtil.TABLE_USER} SET birthday = :birthday WHERE account = :account")
    suspend fun updateUserBirthday(birthday : String,account : Long) : Int
    @Query("UPDATE ${IConstStringUtil.TABLE_USER} SET picture = :picture WHERE account = :account")
    suspend fun updateUserPicture(picture : String,account : Long) : Int
    @Query("UPDATE ${IConstStringUtil.TABLE_USER} SET pwd = :pwd WHERE account = :account")
    suspend fun updateUserPwd(pwd : String,account : Long) : Int

    @Query("SELECT * FROM ${IConstStringUtil.TABLE_USER} WHERE account = :account and pwd = :pwd")
    suspend fun login(account: Long,pwd: String) : User?

    @Query("SELECT EXISTS(SELECT account FROM ${IConstStringUtil.TABLE_USER} WHERE account = :account)")
    suspend fun existAccount(account: Long) : Boolean

    @Query("SELECT EXISTS(SELECT phone FROM ${IConstStringUtil.TABLE_USER} WHERE phone = :phone)")
    suspend fun existPhone(phone: Long) : Boolean
}