package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class MyPageUserInfoApiItem(
    @SerializedName("id") var id:Int,
    @SerializedName("nickname") var nickname: String?=null,
    @SerializedName("profile_image") var profileImage: String?=null,
    @SerializedName("favorite_food") var favoriteFood: ArrayList<String>?=null,
    @SerializedName("introduce") var introduce:String?=null,
    @SerializedName("recipe_count") var recipeCount:Int?=null,
    @SerializedName("story_count") var storyCount:Int?=null,
    @SerializedName("is_follow") var isFollow:Boolean=false,
    @SerializedName("follower_count") var followerCount:Int?=null,
    @SerializedName("following_count") var followingCount:Int?=null
) {
    override fun toString(): String {
        return "MyPageUserInfoApiItem(id=$id, nickname=$nickname, profileImage=$profileImage, favoriteFood=$favoriteFood, introduce=$introduce, recipeCount=$recipeCount, storyCount=$storyCount, followerCount=$followerCount, followingCount=$followingCount)"
    }
}