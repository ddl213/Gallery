package com.example.pagergallery.repository.local.tables.cache

import androidx.room.ColumnInfo
import androidx.room.DeleteColumn
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.TABLE_CACHE
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


@Entity(tableName = TABLE_CACHE)
data class Cache(
    @PrimaryKey val id : Long,
    @ColumnInfo("item")  val item : Item,
    @ColumnInfo("time")  val time : Long

)
class ItemConverter{
    @TypeConverter
    fun fromString(value : String) : Item{
        val type = object : TypeToken<Item>() {}.type
        return Gson().fromJson(value,type)
    }

    @TypeConverter
    fun toString(value : Item) : String{
        return Gson().toJson(value)
    }
}
