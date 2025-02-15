package com.example.pagergallery.repository.local.tables.download

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.util.IConstStringUtil


@Entity(tableName = IConstStringUtil.TABLE_DOWNLOAD)
data class DownLoad(
    @PrimaryKey val id : Long,
    @ColumnInfo("item")  val item : Item,
    @ColumnInfo("time")  val time : Long,
    @ColumnInfo("user_id") val uid : Int

)
