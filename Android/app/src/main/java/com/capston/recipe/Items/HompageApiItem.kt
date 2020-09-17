package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class HompageApiItem (
    @SerializedName("id")var id:Int?=null,
    @SerializedName("multi_image_result")var multiImageResult:ArrayList<MultiImageResultApiItem>?=null,
    @SerializedName("comment_count")val commentCount:Int,
    @SerializedName("likes")val likes:ArrayList<RecipeLikeSimpleVersionAoiItem>,
    @SerializedName("recipe_title")var recipeTitle:String?=null,
    @SerializedName("introduce_recipe")var introduceRecipe:String?=null,
    @SerializedName("food_name")var foodName:String?=null,
    @SerializedName("category")var category:String?=null,
    @SerializedName("for_person") var forPerson:Int?=null,
    @SerializedName("required_time")var requiredTime:Int?=null,
    @SerializedName("created_date")var createdDate:String?=null,
    @SerializedName("type")var type:Boolean,
    @SerializedName("user")val user:UserForSimpleDisplyApiItem?=null,
    @SerializedName("list")val list:ArrayList<RecipeLikeSimpleVersionAoiItem>,
    @SerializedName("hit_count")var hitCount:Int?=0
) {
    override fun toString(): String {
        return "HompageApiItem(id=$id, multiImageResult=$multiImageResult, commentCount=$commentCount, likes=$likes, recipeTitle=$recipeTitle, introduceRecipe=$introduceRecipe, foodName=$foodName, category=$category, forPerson=$forPerson, requiredTime=$requiredTime, createdDate=$createdDate, type=$type, user=$user, list=$list, hitCount=$hitCount)"
    }
}