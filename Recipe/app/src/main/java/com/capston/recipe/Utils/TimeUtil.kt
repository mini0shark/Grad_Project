package com.capston.recipe.Utils

import android.widget.TextView

object TimeUtil{
    fun createdTimeToWhile(view: TextView, time:String){
        var resultTime=time

        view.text = resultTime
    }
}