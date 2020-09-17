package com.capston.recipe.Api.chatApi

import android.app.Activity
import android.content.Context
import com.capston.recipe.Api.RetrofitClient
import com.capston.recipe.Api.chatgApi.ChatApiService
import retrofit2.Retrofit

class ChatService(API_URL:String){
    //    var gsonBuilder = GsonBuilder().registerTypeAdapter(LocalDateTime, )
    val TAG ="ChattingService"
    var retrofit: Retrofit= RetrofitClient.getClient(API_URL)
    var service: ChatApiService
    internal lateinit var result:String
    init {
        service = retrofit.create(ChatApiService::class.java)
    }

}