package com.capston.recipe.Items

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StoryDetailApiItem(
    @SerializedName("id") var id:Int,
    @SerializedName("user")var user:UserForSimpleDisplyApiItem?=null,
    @SerializedName("introduce_recipe")var introduceRecipe:String,
    @SerializedName("multi_image_result")var  multiImageResult:ArrayList<MultiImageResultApiItem>,
    @SerializedName("created_date")var createdDate:String,
    @SerializedName("type")var type:Boolean,
    @SerializedName("recipe_copied")var references:ArrayList<RecipeCopied>,
    @SerializedName("likes")var likes:ArrayList<RecipeLikeSimpleVersionAoiItem>,
    @SerializedName("hit_count")var hitCount:Int=0,
    @SerializedName("comment_container")var comment:ArrayList<CommentForRecipeDetailApiItem>){
    override fun toString(): String {
        return "StoryDetailApiItem(id=$id, user=$user, introduceRecipe='$introduceRecipe', multiImageResult=$multiImageResult, createdDate='$createdDate', type=$type, recipeCopied=$references, likes=$likes, hitCount=$hitCount, commentContainer=$comment)"
    }
}
class RecipeCopied (
    @SerializedName("id")var id: Int? = null,
    @SerializedName("recipe_original")var recipeOriginal: ReferenceNested,
    @SerializedName("recipe_copied")var recipeCopied: ReferenceNested
) {
    class ReferenceNested (

        @SerializedName("id")var id:Int,
        @SerializedName("recipe_title")var recipeTitle:String?=null,
        @SerializedName("food_name")var foodName:String?=null,
        @SerializedName("user")var user:UserForSimpleDisplyApiItem?=null
    ) {
        override fun toString(): String {
            return "ReferenceNested(id=$id, rcipeTitle=$recipeTitle, foodName=$foodName, user=$user)"
        }
    }

    override fun toString(): String {
        return "RecipeCopied(id=$id, recipeOriginal=$recipeOriginal, recipeCopied=$recipeCopied)"
    }
}



