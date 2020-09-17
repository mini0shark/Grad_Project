package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class IngredientApiItem(
    @SerializedName("id") val id:Int?=null,
    @SerializedName("ingredient")val ingredient:String,
    @SerializedName("amount")val amount:String?,
    @SerializedName("recipe_container") val recipeContainer:Int?=null
){
    override fun toString(): String {
        return "IngredientApiItem(id=$id, ingredient='$ingredient', amount=$amount, recipeContainer=$recipeContainer)"
    }
}