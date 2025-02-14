package com.example.pagergallery.unit.util

import android.os.Parcelable
import com.tencent.mmkv.MMKV

object KeyValueUtils {
    private val mmkv = MMKV.defaultMMKV()

    fun getInt(key : String,default : Int = 0) = mmkv.getInt(key,default)
    fun setInt(key: String,value : Int){
        mmkv.encode(key,value)
    }

    fun getLong(key : String,default : Long = 0L) = mmkv.getLong(key,default)
    fun setLong(key: String,value : Long){
        mmkv.encode(key,value)
    }

    fun getFloat(key : String,default : Float = 0f) = mmkv.getFloat(key,default)
    fun setFloat(key: String,value : Float){
        mmkv.encode(key,value)
    }

    fun getDouble(key : String,default : Double = 0.0) = mmkv.decodeDouble(key,default)
    fun setDouble(key: String,value : Double){
        mmkv.encode(key,value)
    }

    fun getBoolean(key : String, default : Boolean = false) = mmkv.getBoolean(key,default)
    fun setBoolean(key: String,value : Boolean){
        mmkv.encode(key,value)
    }

    fun getString(key : String,default : String = "") = mmkv.getString(key,default)
    fun setString(key: String,value : String){
        mmkv.encode(key,value)
    }

    fun<T:Parcelable> getParcelable(key : String,clazz: Class<T>) = mmkv.decodeParcelable(key,clazz)
    fun setParcelable(key: String,value : Parcelable){
        mmkv.encode(key,value)
    }


    //删除
    fun removeValueForKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    fun removeValuesForKeys(key: Array<String>) {
        mmkv.removeValuesForKeys(key)
    }

    fun clear() {
        mmkv.clearAll()
    }

}