package com.capston.recipe.Items

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FollowersApiItem(
    @SerializedName("id") @Expose var id:Int,
    @SerializedName("password") @Expose var password:String?=null,
    @SerializedName("last_login") @Expose var lastLogin:String?=null,
    @SerializedName("user_id") @Expose var user_id:String?=null,
    @SerializedName("name") @Expose var name:String?=null,
    @SerializedName("introduce") @Expose var introduce:String?=null,
    @SerializedName("join_date") @Expose var joinDate:String?=null,
    @SerializedName("favorite_food") @Expose var favoriteFood:String?=null,
    @SerializedName("nickname") @Expose var nickname:String?=null,
    @SerializedName("member_type") @Expose var memberType:Int?=null,
    @SerializedName("profile_image") @Expose var profileImage:String?=null,
    @SerializedName("followers") @Expose var followers:ArrayList<Int>?=null
)