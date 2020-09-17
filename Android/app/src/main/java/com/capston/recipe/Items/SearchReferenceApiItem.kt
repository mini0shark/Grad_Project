package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class SearchReferenceApiItem(
    @SerializedName("id") val id:Int,
    @SerializedName("recipe_title") val recipeTitle:String,
    @SerializedName("food_name") val foodName:String,
    @SerializedName("multi_image_result") val multiImageResult:String?=null,
    @SerializedName("user") val user:SearchRefUser,
    @SerializedName("hit_count") val hitCount:Int,
    @SerializedName("likes_count") val likesCount:Int
) {
    override fun toString(): String {
        return "SearchReferenceApiItem(id=$id, recipeTitle='$recipeTitle', foodName='$foodName', multiImageResult=$multiImageResult, user=$user, hitCount=$hitCount, likesCount=$likesCount)"
    }
}

class SearchRefUser(
    @SerializedName("id") val id:Int,
    @SerializedName("nickname") val nickname:String
) {
    override fun toString(): String {
        return "SearchRefUser(id=$id, nickname='$nickname')"
    }
}