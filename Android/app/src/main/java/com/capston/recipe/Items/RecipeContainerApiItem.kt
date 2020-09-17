package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

open class RecipeContainerApiItem(

    @SerializedName("id")var id:Int?=null,
    @SerializedName("recipe_title")var recipeTitle:String?=null,
    @SerializedName("food_name")var foodName:String?=null,
    @SerializedName("introduce_recipe")var introduceRecipe:String?=null,
    @SerializedName("category")var category:String?=null,
    @SerializedName("for_person")var forPerson:Int?=null,
    @SerializedName("required_time")var requiredTime:Int?=null,
    @SerializedName("created_date")var createdDate:String?=null,
    @SerializedName("type")var type:Boolean?=null,
    @SerializedName("user")var user:Int?=null,
    @SerializedName("likes")val likes:ArrayList<Integer>?=null,
    @SerializedName("list")val list:ArrayList<Int>?=null,
    @SerializedName("hit_count")var hitCount:Int?=0
){
    override fun toString(): String {
        return "RecipeContainerApiItem(id=$id, recipeTitle=$recipeTitle, foodName=$foodName, introduceRecipe=$introduceRecipe, category=$category, forPerson=$forPerson, requiredTime=$requiredTime, createdDate=$createdDate, type=$type, user=$user, likes=$likes, list=$list, hitCount=$hitCount)"
    }
}