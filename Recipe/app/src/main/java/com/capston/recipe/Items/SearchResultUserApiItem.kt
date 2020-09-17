package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class SearchResultUserApiItem(
    @SerializedName("id")var id:Int?=null,
    @SerializedName("user_id")var userId:String?=null,
    @SerializedName("nickname")var nickname:String?=null,
    @SerializedName("profile_image")var profileImage:String?=null,
    @SerializedName("post_count")var postCount:Int,
    @SerializedName("follower_count")var followerCount:Int
) {
    override fun toString(): String {
        return "SearchResultUserApiItem(id=$id, userId=$userId, nickname=$nickname, profileImage=$profileImage, postCount=$postCount, followerCount=$followerCount)"
    }
}