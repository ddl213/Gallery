package com.example.pagergallery.unit.manager

import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.KeyValueUtils

object DownloadManager {
    const val DOWNLOAD_ITEM = "download_item"


    fun getDownload(uid : Int) : List<Item>{
        return listOf()
    }

    fun saveDownload(item: Item){
        //val


        KeyValueUtils.setParcelable(DOWNLOAD_ITEM,item)

    }

    fun removeDownload(item : Item){

    }

    fun clear(){

    }

}