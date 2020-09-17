package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class SearchResultRecipeApiItem (
    @SerializedName("id")var id:Int,
    @SerializedName("recipe_title")var title:String,
    @SerializedName("food_name")var foodName:String,
    @SerializedName("multi_image_result")var multiImageResult:String,
    @SerializedName("copied_count")var copiedCount:Int,
    @SerializedName("likes_count")var likesCount:Int
) {
    override fun toString(): String {
        return "SearchResultRecipeApiItem(id=$id, title='$title', foodName='$foodName', multiImageResult='$multiImageResult', copiedCount=$copiedCount, likesCount=$likesCount)"
    }
}