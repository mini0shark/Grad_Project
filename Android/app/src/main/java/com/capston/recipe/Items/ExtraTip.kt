package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class ExtraTip(
    @SerializedName("id")var id:Int?=null,
    @SerializedName("tip")var tip:String,
    @SerializedName("recipe_container")var recipeContainer:Int?=null){
    override fun toString(): String {
        return "ExtraTip(id=$id, tip='$tip', recipeContainer=$recipeContainer)"
    }
}