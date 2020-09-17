package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class HistoryObjectApiItem (
    @SerializedName("id")var id:Int,
    @SerializedName("recipe_title")var recipeTitle:String?=null,
    @SerializedName("multi_image_result")var multiImageResult:String?=null
) {
    class FollowingModel(
        @SerializedName("id")var id:Int,
        @SerializedName("recipe_title")var recipeTitle:String?,
        @SerializedName("multi_image_result")var image:String
    ) {
        override fun toString(): String {
            return "FollowingModel(id=$id, recipeTitle=$recipeTitle, image='$image')"
        }
    }

    override fun toString(): String {
        return "HistoryObjectApiItem(id=$id, recipeTitle=$recipeTitle, multiImageResult=$multiImageResult)"
    }

}