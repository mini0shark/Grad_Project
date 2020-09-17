package com.capston.recipe.Items

import com.google.gson.annotations.SerializedName

class CommentApiItem(
    @SerializedName("id") val id:Int?=null,
    @SerializedName("created_date") val createdDate:String?=null,
    @SerializedName("text") val text:String,
    @SerializedName("recipe_container_id") val recipeContainer:Int,
    @SerializedName("user_id") val user:Int,
    @SerializedName("tag") val tag:ArrayList<Int>?=null,
    @SerializedName("likes") val userLike:ArrayList<Int>?=null
) {
    override fun toString(): String {
        return "CommentApiItem(id=$id, createdDate=$createdDate, text='$text', recipeContainer=$recipeContainer, user=$user, tag=$tag, userLike=$userLike)"
    }
//    fun toCommentReceiveApiItem():CommentReceiveApiItem{
//        val likeArray = arrayListOf<LikeApiItem>()
//        if(userLike!=null){
//            for(like in userLike) {
//                likeArray.add(LikeApiItem(id=like))
//            }
//        }
//        return CommentReceiveApiItem(id = id,createdDate = createdDate, text = text,
//            recipeContainer = RecipeContainerApiItem(id= recipeContainer),user = UserApiItemV2(id= id),likes = likeArray)
//    }
    fun likeToWrapper():ArrayList<LikeApiItem>{
        val likeArray = arrayListOf<LikeApiItem>()
        if(userLike!=null){
            for(like in userLike) {
                likeArray.add(LikeApiItem(id=like))
            }
        }
        return likeArray
    }
}