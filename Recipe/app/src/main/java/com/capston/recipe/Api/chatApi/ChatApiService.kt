package com.capston.recipe.Api.chatgApi

import com.capston.recipe.Items.ChattingApiItem
import com.capston.recipe.Items.ChattingListApiItem
import com.capston.recipe.Items.RecipeContainerApiItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ChatApiService {
    ////////////////////////////////////post////////////////////////////////////////////////////////
    // Register
    @GET("chatting/getChattingBefore")
    fun getLastChatting(
        @QueryMap queryMap: HashMap<String, Int>
    ): Call<ChattingApiItem?>
    @GET("chatting/getChattingList")
    fun getChattingList(
        @Query("user_id") userId:Int
    ): Call<ArrayList<ChattingListApiItem>>
    @GET("chatting/getIsNew/{user_id}/{message_id}")
    fun getIsNew(
        @Path("message_id") messageId:Int,
        @Path("user_id") userId:Int
    ): Call<Boolean>
}
