package com.example.pagergallery.unit

import android.os.Parcelable
import com.tencent.mmkv.MMKV

object KeyValueUtils {
    private val mmkv = MMKV.defaultMMKV()

    fun getInt(key : String) = mmkv.getInt(key,-100)
    fun setInt(key: String,value : Int){
        mmkv.decodeInt(key,value)
    }

    fun getLong(key : String) = mmkv.getLong(key,-100)
    fun setLong(key: String,value : Long){
        mmkv.decodeLong(key,value)
    }

    fun getFloat(key : String) = mmkv.getFloat(key,-100f)
    fun setFloat(key: String,value : Float){
        mmkv.decodeFloat(key,value)
    }

    fun getDouble(key : String) = mmkv.decodeDouble(key,-100.0)
    fun setDouble(key: String,value : Double){
        mmkv.encode(key,value)
    }

    fun getBoolean(key : String) = mmkv.getBoolean(key,false)
    fun setBoolean(key: String,value : Boolean){
        mmkv.decodeBool(key,value)
    }

    fun getString(key : String) = mmkv.getString(key,"")
    fun setString(key: String,value : String){
        mmkv.decodeString(key,value)
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