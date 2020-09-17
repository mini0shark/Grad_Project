package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class RecipeDetailApiItem(
    @SerializedName("id")var id:Int?=null,
    @SerializedName("recipe_order")var recipeOrder:ArrayList<RecipeApiItem>?,
    @SerializedName("multi_image_result")var multiImageResult:ArrayList<MultiImageResultApiItem>?=null,
    @SerializedName("extra_tip")val extraTip:ArrayList<ExtraTip>?=null,
    @SerializedName("comment_container")val comment:ArrayList<CommentForRecipeDetailApiItem>,
    @SerializedName("ingredients") var ingredients:ArrayList<IngredientApiItem>?=null,
    @SerializedName("likes")val likes:ArrayList<RecipeLikeSimpleVersionAoiItem>,
    @SerializedName("user")val user:UserForSimpleDisplyApiItem?=null,
    @SerializedName("recipe_title")var recipeTitle:String?=null,
    @SerializedName("food_name")var foodName:String?=null,
    @SerializedName("introduce_recipe")var introduceRecipe:String?=null,
    @SerializedName("category")var category:String?=null,
    @SerializedName("for_person") var forPerson:Int?=null,
    @SerializedName("required_time")var requiredTime:Int?=null,
    @SerializedName("created_date")var createdDate:String?=null,
    @SerializedName("type")var type:Boolean,
    @SerializedName("hit_count")var hitCount:Int?=0,
    @SerializedName("list")val list:ArrayList<RecipeLikeSimpleVersionAoiItem>
) {
    override fun toString(): String {
        return "HomePageApiItem(id=$id, recipeOrder=$recipeOrder, multiImageResult=$multiImageResult, comment=$comment, ingredients=$ingredients, likes=$likes, recipeTitle=$recipeTitle, foodName=$foodName, introduceRecipe=$introduceRecipe, category=$category, forPerson=$forPerson, requiredTime=$requiredTime, createdDate=$createdDate, type=$type, user=$user, list=$list, hitCount=$hitCount)"
    }
}