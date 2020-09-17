package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class UserApiItem(
    @SerializedName("id")var id:Int?=null,
    @SerializedName("password") var password:String?=null,
    @SerializedName("last_login")var lastLogin:String?=null,
    @SerializedName("user_id")var userId:String?=null,
    @SerializedName("name")var name:String?=null,
    @SerializedName("introduce")var introduce:String?=null,
    @SerializedName("join_date")var joinDate:String?=null,
    @SerializedName("favorite_food")var favoriteFood:String?=null,
    @SerializedName("nickname")var nickname:String?=null,
    @SerializedName("member_type")var memberType:Int?=null,
    @SerializedName("profile_image")var profileImage:String?=null,
    @SerializedName("followers")var followers:ArrayList<FollowersApiItem>?=null
){
    override fun toString(): String {
        return "UserApiItem(id=$id, password=$password, lastLogin=$lastLogin, userId=$userId, name=$name, introduce=$introduce, joinDate=$joinDate, favoriteFood=$favoriteFood, nickname=$nickname, memberType=$memberType, profileImage=$profileImage, followers=$followers)"
    }
    fun toV2():UserApiItemV2{
        val v2 = UserApiItemV2(id, password, lastLogin, userId, name, introduce, joinDate, favoriteFood, nickname, memberType, profileImage)
        val followerList = arrayListOf<Int>()
        if(followers!=null){
            for(follower in followers!!) {
                followerList.add(follower.id)
            }
        }
        v2.followers = followerList
        return v2
    }
}