package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class RegisterRecipeApiItem(@SerializedName("recipe_container")val recipeContainerApiItem: RecipeContainerApiItem,
                            @SerializedName("ingredients")val ingredientApiItem: ArrayList<IngredientApiItem>?=null,
                            @SerializedName("recipes")val recipeApiItem: ArrayList<RecipeApiItem>?=null,
                            @SerializedName("extra_tip")val extraTip: ArrayList<ExtraTip>?=null
                             ){
    override fun toString(): String {
        return "RegisterRecipeApiItem(recipeContainerApiItem=$recipeContainerApiItem, ingredientApiItem=$ingredientApiItem, recipeApiItem=$recipeApiItem, extratip=$extraTip)"
    }
}