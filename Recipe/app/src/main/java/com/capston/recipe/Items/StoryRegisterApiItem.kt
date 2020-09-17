package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class StoryRegisterApiItem (
    @SerializedName("content")val content: String,
    @SerializedName("references")val references: ArrayList<Int>     // extratip에 저장
){
    override fun toString(): String {
        return "RegisterRecipeApiItem( content=$content, references=$references)"
    }
}