package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class UserForSimpleDisplyApiItem(
    @SerializedName("id")var id:Int=-1,
    @SerializedName("user_id")var userId:String?=null,
    @SerializedName("nickname")var nickname:String?=null,
    @SerializedName("profile_image")var profileImage:String?=null
) {
    override fun toString(): String {
        return "SimpleUserForHomePageApiItem(id=$id, userId=$userId, nickname=$nickname, profileImage=$profileImage)"
}
}