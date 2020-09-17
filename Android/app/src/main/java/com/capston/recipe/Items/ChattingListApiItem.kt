package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class ChattingListApiItem (
    @SerializedName("id") val id:Int,
    @SerializedName("room") val room:Int,  //채팅방 이름 있을 예정
    @SerializedName("type") val type:Int,   // 1-> 2->
    @SerializedName("message") val message:String?=null,
    @SerializedName("user") val userId:Int?=null,
    @SerializedName("check") val check:Boolean,
    @SerializedName("user_list") val user_list:ArrayList<UserForSimpleDisplyApiItem>?=null
) {
    override fun toString(): String {
        return "ChattingListApiItem(id=$id, room=$room, type=$type, message=$message, user=$user_list, check=$check)"
    }
}