package com.capston.recipe.Utils

import android.content.Context
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.USER

object GettingUser {
    lateinit var sft: SharedPreferenceTool
    lateinit var user: UserApiItem
    fun getUserInfo(context: Context):UserApiItem?{
        sft = SharedPreferenceTool(context!!)
        return sft.loadObjectSharedPreference<UserApiItem>(USER)

    }
}