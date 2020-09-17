package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class HistoryApiItem(
    @SerializedName("id") val id:Int,
    @SerializedName("object") var obj:HistoryObjectApiItem?,
    @SerializedName("classify") var classify:String,
    @SerializedName("count") var count:Int=0,
    @SerializedName("content") var content:String,
    @SerializedName("user") var user:UserForSimpleDisplyApiItem,
    @SerializedName("update_time") var updateTime:String
) {
    override fun toString(): String {
        return "HistoryApiItem(id=$id, obj=$obj, classify='$classify', count=$count, content='$content', user=$user, updateTime='$updateTime')"
    }
}
