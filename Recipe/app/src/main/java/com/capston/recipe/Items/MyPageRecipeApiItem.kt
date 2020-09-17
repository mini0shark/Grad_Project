package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class MyPageRecipeApiItem(
    @SerializedName("id") var id:Int,
    @SerializedName("multi_image_result") var multiImageResult:String?,
    @SerializedName("image_count") var imageCount:Int,
    @SerializedName("food_name") var foodName:String
) {
    override fun toString(): String {
        return "MyPageRecipeApiItem(id=$id, multiImageResult='$multiImageResult', imageCount=$imageCount)"
    }
}