package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class MultiImageResultApiItem(
    @SerializedName("id")val id:Int,
    @SerializedName("image")val image:String,
    @SerializedName("recipe_container")val recipeContainer:Int
) {
    override fun toString(): String {
        return "MultiImageResultApiItem(id=$id, image='$image', recipeContainer=$recipeContainer)"
    }
}
