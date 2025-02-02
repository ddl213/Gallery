package com.example.pagergallery.repository.api

import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Pixabay(
    val total : Int,
    val totalHits : Int,
    val hits : Array<Item>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pixabay

        if (total != other.total) return false
        if (totalHits != other.totalHits) return false
        if (!hits.contentEquals(other.hits)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = total
        result = 31 * result + totalHits
        result = 31 * result + hits.contentHashCode()
        return result
    }
}

@Parcelize
data class Item(
    @SerializedName("id") var id : Long,
    @SerializedName("webformatURL") var webFormatURL : String,
    @SerializedName("largeImageURL") var largeUrl : String,
    @SerializedName("webformatHeight") var webImageHeight : Int,
    @SerializedName("imageHeight") var largeImageHeight : Int,
    @SerializedName("type") var type : String,
    @SerializedName("tags") var tags : String,
    @SerializedName("user_id") var userId : Int,
    @SerializedName("user") var userName : String,
    @SerializedName("userImageURL") var userImage : String,
    @Ignore var localUrl : String,
    @Ignore var isCollected : Boolean
):Parcelable{
    constructor() : this(0,"","",0,0,"","",0,"","","",false)
}
