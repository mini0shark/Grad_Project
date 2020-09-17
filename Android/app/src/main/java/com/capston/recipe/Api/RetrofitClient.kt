package com.capston.recipe.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    lateinit var retrofit: Retrofit
    fun getClient(API_URL:String):Retrofit{
        retrofit= Retrofit.Builder().
            baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }


}