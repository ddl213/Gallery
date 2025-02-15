package com.example.pagergallery.repository.local.tables.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pagergallery.unit.util.IConstStringUtil
import com.google.gson.annotations.SerializedName

@Entity(tableName = IConstStringUtil.TABLE_USER, indices = [Index(value = ["account","phone"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) @field:SerializedName("id") val id : Int?,
    @field:SerializedName( "account") val account : Long,
    @field:SerializedName( "name") var name : String?,
    @field:SerializedName( "pwd") var pwd : String,
    @field:SerializedName( "phone") var phone : Long,
    @field:SerializedName( "sex") var sex: String? = "",
    @field:SerializedName( "birthday") var birthday: String? = "",
    @field:SerializedName( "picture") var picture : String? = ""
)
