package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class RecipeResultImageApiItem(
    @SerializedName("id")val id:Int?,
    var image:String,
    @SerializedName("recipe_container")val recipeContainer:Int?
)