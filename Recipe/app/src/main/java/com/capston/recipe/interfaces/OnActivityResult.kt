package com.capston.recipe.interfaces

import android.content.Intent
import com.capston.recipe.Items.AddImageItem

interface OnActivityResult {
    fun onIntentResult(resultCode:Int, requestCode:Int, data: Intent, addImageItem:ArrayList<AddImageItem>)
}