package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class StoryHistoryApiItem (
    @SerializedName("id")var id:Int,
    @SerializedName("multi_image_result")var multiImageResult:String?=null,
    @SerializedName("introduce_recipe")var introduce:String?=null,
    @SerializedName("created_date") var updateTime:String,
    @SerializedName("user")var user:UserForSimpleDisplyApiItem?=null
)
