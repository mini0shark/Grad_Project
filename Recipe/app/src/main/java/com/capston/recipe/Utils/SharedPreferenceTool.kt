package com.capston.recipe.Utils

import android.app.Activity
import android.content.Context
import com.capston.recipe.Items.RecipeDetailApiItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.ArrayList

class SharedPreferenceTool(val context: Context){
    fun <T> saveObjectSharedPreference(key:String, value:T){
        val preference = context.getSharedPreferences("pre",Activity.MODE_PRIVATE)
        val editor = preference.edit()
        val gson = Gson()
        val json =gson.toJson(value)
        editor.putString(key, json).apply()
    }
    inline fun <reified T> loadObjectSharedPreference(key:String):T?{
        val preference = context.getSharedPreferences("pre",Activity.MODE_PRIVATE)
        val gson = Gson()
        val json = preference.getString(key, "")
        return if(json=="")
            null
        else
            gson.fromJson(json, T::class.java)
    }

    fun loadHomeItems():ArrayList<RecipeDetailApiItem>?{
        val key = "homeItems"
        val type = object : TypeToken<ArrayList<RecipeDetailApiItem>>() {}.type
        val preference = context.getSharedPreferences("pre",Activity.MODE_PRIVATE)
        val gson = Gson()
        val json = preference.getString(key, "")
        val res = gson.fromJson<ArrayList<RecipeDetailApiItem>>(json, type)
        return if(json=="")
            null
        else
            res
    }

    fun saveSharedPreference(key:String, value:String?){
        val preference = context.getSharedPreferences("pre",Activity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString(key,value).apply()
    }
    fun loadSharedPreference(key:String):String?{
        val preference = context.getSharedPreferences("pre",Activity.MODE_PRIVATE)
        return preference.getString(key,"")
    }
}