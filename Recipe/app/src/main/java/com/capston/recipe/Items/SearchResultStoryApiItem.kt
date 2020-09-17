package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class SearchResultStoryApiItem (
    @SerializedName("id")var id:Int,
    @SerializedName("introduce_recipe")var introduceRecipe:String,
    @SerializedName("multi_image_result")var multiImageResult:String,
    @SerializedName("likes_count")var likesCount:Int
) {
    override fun toString(): String {
        return "SearchResultStoryApiItem(id=$id, introduceRecipe='$introduceRecipe', multiImageResult='$multiImageResult', likesCount=$likesCount)"
    }
}