package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class CommentForRecipeDetailApiItem(
    @SerializedName("id") val id:Int?=null,
    @SerializedName("user") val user:UserForSimpleDisplyApiItem,
    @SerializedName("created_date") val createdDate:String?=null,
    @SerializedName("text") val text:String,
    @SerializedName("recipe_container_id") val recipeContainer:Int?=null,
    @SerializedName("tag") val tag:ArrayList<Int>?=null,
    @SerializedName("likes") val userLike:ArrayList<Int>?=null
) {
    override fun toString(): String {
        return "CommentForRecipeDetailApiItem(id=$id, user=$user, createdDate=$createdDate, text='$text', recipeContainer=$recipeContainer, tag=$tag, userLike=$userLike)"
    }
}