package com.capston.recipe.Api.chatApi

import com.capston.recipe.Items.HistoryApiItem
import com.capston.recipe.Items.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RecipeApiService {
    ////////////////////////////////////post////////////////////////////////////////////////////////
    // Register
    @Multipart
    @POST("recipeApi/{user_id}/recipeContainer")
    fun postRegisterRecipe(@Path("user_id") user:Int,
                           @Part("recipeContainer") registerRecipeApiItem: RegisterRecipeApiItem,
                           @Part images:ArrayList<MultipartBody.Part>
    ): Call<ResponseBody>

    @Multipart
    @POST("recipeApi/recipeContainer/{recipe_id}")
    fun postEditionRegisterRecipe(@Path("recipe_id") recipeId:Int,
                                  @Part("recipeContainer") registerRecipeApiItem: RegisterRecipeApiItem,
                                  @Part images:ArrayList<MultipartBody.Part>
    ): Call<ResponseBody>
    @Multipart
    @POST("recipeApi/{user_id}/story")
    fun postRegisterStory(@Path("user_id") user:Int,
                          @Part("story_container") storyContent: StoryRegisterApiItem,
                          @Part images:ArrayList<MultipartBody.Part>
    ): Call<ResponseBody>
    // editStory
    @Multipart
    @POST("recipeApi/recipeContainer/{recipe_id}")
    fun postEditStory(@Path("recipe_id") recipeId:Int,
                      @Part("story_container") storyContent: StoryRegisterApiItem,
                      @Part images:ArrayList<MultipartBody.Part>
    ): Call<ResponseBody>
    // RecipeDetail
    @POST("recipeApi/registerRecipeComment")
    fun postRegisterRecipeComment(@Body comment:CommentApiItem):Call<CommentForRecipeDetailApiItem>

    // FragmentHome, RecipeDetail
    @POST("recipeApi/like/{user_id}/{recipe_id}")
    fun postClickRecipeLike(@Path("user_id")userId:Int,
                            @Path("recipe_id")recipeId:Int,
                            @Body like:Boolean
    ):Call<Boolean>
    // RecipeDetail
    @POST("recipeApi/commentLike/{user_id}/{comment_id}")
    fun postClickCommentLike(@Path("user_id")userId: Int,
                             @Path("comment_id")commentId: Int):Call<Boolean>


    // List 추가
    @Multipart
    @POST("recipeApi/WishList")
    fun postWishList(@Part("user_id") userId:Int,
                     @Part("recipe_id") recipeId:Int
    ):Call<Boolean>




    ////////////////////////////////////get/////////////////////////////////////////////////////////

    /////////////////////1////////////////////
    // Homepage?
    @GET("recipeApi/{pk}/recipe")
    fun getRecipeContainer(@Path("pk") userId:Int): Call<Array<RecipeContainerApiItem>>


    @GET("recipeApi/recipeList/{user_id}")
    fun getHomeListItems(@Path("user_id") user_id:String, @Query("order") order:Int): Call<ArrayList<HompageApiItem>>


    @GET("recipeApi/{user_id}")
    fun getHomeItems(@Path("user_id") userId:Int): Call<RecipeDetailApiItem>

    @GET("recipeApi/recipeDetail/{recipe_id}")
    fun getRecipeItem(@Path("recipe_id") recipeId:Int):Call<RecipeDetailApiItem>

    @GET("recipeApi/storyDetail/{recipe_id}")
    fun getStoryItem(@Path("recipe_id") recipeId:Int):Call<StoryDetailApiItem>

    /////////////////////2////////////////////
    @GET("recipeApi/searchInit/{user_id}")
    fun getRecipeSearchItems(@Path("user_id") userId:Int?,
                             @Query("order") order:Int
    ):Call<ArrayList<RecipeContainerSimpleApiItem>>
    @GET("recipeApi/searchRecipe")
    fun getRecipeSearchWithQuery(@Query("query") query:String?
    ):Call<ArrayList<SearchResultRecipeApiItem>>
    @GET("recipeApi/searchStory")
    fun getStorySearchWithQuery(@Query("query") query:String?
    ):Call<ArrayList<SearchResultStoryApiItem>>

    @GET("recipeApi/Recipe/{recipe_id}")
    fun getRecipe(@Path("recipe_id")recipeId: Int):Call<RecipeDetailApiItem>


    /////////////////////3////////////////////
    // SearchReference에서 검색으로 아이템 가져올때
    @GET("recipeApi/searchReference")
    fun getReferences(@Query("searchText")searchText:String):Call<ArrayList<SearchReferenceApiItem>>
    // SearchReference에서 내 레시피 가져올 때
    @GET("recipeApi/searchReferenceMyRecipes/{user_id}")
    fun getMyReferences(@Path("user_id") userId:Int):Call<ArrayList<SearchReferenceApiItem>>


    ////////////////////////4////////////////////
    // History 리스트 가져오기
    @GET("recipeApi/history/{user_id}")
    fun getMyHistory(@Path("user_id") userId:Int,@Query("order")order: Int):Call<ArrayList<HistoryApiItem>>

    @GET("recipeApi/history/copiedList/{recipe_id}")
    fun getCopiedList(@Path("recipe_id") recipeId:Int):Call<ArrayList<StoryHistoryApiItem>>


    //////////////////5/////////////////
    // MyPage하단 Recipe사진 가져오기
    @GET("recipeApi/myPage/items/{user_id}/{type}")
    fun getMyPageItem(@Path("user_id") userId:Int,
                      @Path("type") type:String):Call<ArrayList<MyPageRecipeApiItem>>

    ////////////////////////////////////delete//////////////////////////////////////////////////////
    @DELETE("recipeApi/Comment/{comment_id}")
    fun deleteComment(@Path("comment_id")commentId: Int):Call<ResponseBody>
    @DELETE("recipeApi/Recipe/{id}")
    fun deleteRecipe(@Path("id")recipeId: Int):Call<ResponseBody>
    /////////////////////////////////////put////////////////////////////////////////////////////////

}
//    @Multipart
//    @POST("recipeApi/registerRecipeStep")
//    fun postRegisterRecipeStep(@Part("recipe") recipe:RecipeApiItem,
//                               @Part resultImage:ArrayList<MultipartBody.Part>): Call<ResponseBody>