package com.example.pagergallery.repository.local.tables.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pagergallery.unit.TABLE_COLL_NAME

@Entity(tableName = TABLE_COLL_NAME)
data class Collection(
    @PrimaryKey val id : Long,
    @ColumnInfo("hits") val hits: String,
    @ColumnInfo("time")  val time : Long,
    @ColumnInfo("user_id") val uid : Int
)

data class Hit(
    @ColumnInfo("id") val id : Long,
    @ColumnInfo("webFormatURL") val webFormatURL : String,
    @ColumnInfo("largeImageURL") val largeUrl : String,
    @ColumnInfo("webFormatHeight") val webImageHeight : Int,
    @ColumnInfo("imageHeight") val largeImageHeight : Int,
    @ColumnInfo("type") val type : String,
    @ColumnInfo("tags") val tags : String,
    @ColumnInfo("user_id") val userId : Int,
    @ColumnInfo("user") val userName : String,
    @ColumnInfo("userImageURL") val userImage : String
)

