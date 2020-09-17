package com.capston.recipe.Items


import com.google.gson.annotations.SerializedName




class RecipeLikeSimpleVersionAoiItem (
    @SerializedName("user") val userId: Int? = null
) {
    override fun toString(): String {
        return "SimpleLikeForHomePageAoiItem(user=$userId)"
    }
}
