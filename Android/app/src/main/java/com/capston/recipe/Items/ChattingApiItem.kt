package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class ChattingApiItem (
    @SerializedName("user") val user:UserForSimpleDisplyApiItem,
    @SerializedName("msg_list") val msgList:ArrayList<Message>){

    inner class Message (
        @SerializedName("id") val id:Int,
        @SerializedName("room") val room:Int,  //채팅방 이름 있을 예정
        @SerializedName("type") val type:Int,   // 1-> 2->
        @SerializedName("message") val message:String?=null,
        @SerializedName("user") val user:UserForSimpleDisplyApiItem?=null,
        @SerializedName("check") val check:Boolean
    )
}