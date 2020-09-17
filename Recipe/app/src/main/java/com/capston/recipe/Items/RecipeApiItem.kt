package com.capston.recipe.Items

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

class RecipeApiItem(@SerializedName("id")val id:Int?=null,
                    @SerializedName("order")var order:Int,
                    @SerializedName("image")var image:String?=null,
                    @SerializedName("explain")var explain:String?,
    @SerializedName("recipe_container")val recipeContainer:Int?=null)
{
    override fun toString(): String {
        return "RecipeApiItem(id=$id, order=$order, image=$image, explain=$explain, recipeContainer=$recipeContainer)"
    }
}