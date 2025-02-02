package com.example.pagergallery.repository.local.tables.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pagergallery.unit.TABLE_USER
import com.example.pagergallery.unit.base.dao.BaseDao

@Dao
interface UserDao : BaseDao<User>{


    @Query("DELETE FROM $TABLE_USER WHERE account = :account")
    suspend fun deleteUserByAccount(account : Long) : Int

    @Query("UPDATE $TABLE_USER SET name = :name WHERE account = :account")
    suspend fun updateUserName(name : String,account : Long) : Int
    @Query("UPDATE $TABLE_USER SET phone = :phone WHERE account = :account")
    suspend fun updateUserPhone(phone : Long,account : Long) : Int
    @Query("UPDATE $TABLE_USER SET sex = :sex WHERE account = :account")
    suspend fun updateUserSex(sex : String,account : Long) : Int
    @Query("UPDATE $TABLE_USER SET birthday = :birthday WHERE account = :account")
    suspend fun updateUserBirthday(birthday : String,account : Long) : Int
    @Query("UPDATE $TABLE_USER SET picture = :picture WHERE account = :account")
    suspend fun updateUserPicture(picture : String,account : Long) : Int
    @Query("UPDATE $TABLE_USER SET pwd = :pwd WHERE account = :account")
    suspend fun updateUserPwd(pwd : String,account : Long) : Int

    @Query("SELECT * FROM $TABLE_USER WHERE account = :account and pwd = :pwd")
    suspend fun login(account: Long,pwd: String) : User?

    @Query("SELECT EXISTS(SELECT account FROM $TABLE_USER WHERE account = :account)")
    suspend fun existAccount(account: Long) : Boolean
}