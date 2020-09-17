package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class StoryItemForSearchApiItem (
    @SerializedName("id") val id:Int,
    @SerializedName("introduce") val introduce:String,
    @SerializedName("multi_image_result") val multiImageResult:String?=null,
    @SerializedName("user") val user:SearchRefUser,
    @SerializedName("hit_count") val hitCount:Int,
    @SerializedName("likes_count") val likesCount:Int
) {
    override fun toString(): String {
        return "StoryItemForSearchApiItem(id=$id, introduce='$introduce', multiImageResult=$multiImageResult, user=$user, hitCount=$hitCount, likesCount=$likesCount)"
    }
}