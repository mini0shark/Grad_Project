package com.capston.recipe.Items

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FollowUserApiItem (
    @SerializedName("id") @Expose var id:Int,
    @SerializedName("nickname") @Expose var nickname:String?=null,
    @SerializedName("profile_image") @Expose var profileImage:String?=null,
    @SerializedName("created_time") @Expose var followTime:String?=null
)