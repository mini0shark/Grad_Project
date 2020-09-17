package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class RecipeContainerSimpleApiItem (
    @SerializedName("id")var id:Int,
    @SerializedName("multi_image_result")var multiImageResult:String?=null,
    @SerializedName("recipe_title")var recipeTitle:String?=null,
    @SerializedName("food_name")var foodName:String?=null,
    @SerializedName("likes_count")var likesCount:Int?=null,
    @SerializedName("user")var user:Int?=null
) {
    override fun toString(): String {
        return "SimpleRecipeConatinerApiItem(id=$id, multiImageResult=생략, recipeTitle=$recipeTitle, foodName=$foodName)"
    }
}