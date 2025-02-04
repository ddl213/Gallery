package com.example.pagergallery.repository.local.tables.query

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pagergallery.unit.TABLE_QUERY_NAME

@Entity(tableName = TABLE_QUERY_NAME,
    indices = [Index(value = ["queryStr"], unique = true)])
data class HistoryQuery(
    @PrimaryKey(autoGenerate = true) val id : Int?,
    @ColumnInfo(name = "queryStr") val queryStr : String,
    @ColumnInfo(name = "time") var time : Long
)

