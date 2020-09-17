package com.capston.recipe.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.capston.recipe.R
import com.capston.recipe.Utils.ConstCodes.NO_IMAGE_ADDRESS
import com.capston.recipe.Utils.ImageUtils

class T3ImageViewPagerAdapter(val context:Context, private val imageList:ArrayList<String?>): PagerAdapter() {
    override fun isViewFromObject(view: View, o: Any): Boolean {
        return (view == o)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater:LayoutInflater= LayoutInflater.from(context)
        var view:View
        view = layoutInflater.inflate(R.layout.layout_ta_image, null)
        val imageView:ImageView = view.findViewById(R.id.imageView_recyclerHomeImage)
        val imageUri:String = if(imageList[position]!=null)
            imageList[position]!!
        else
            NO_IMAGE_ADDRESS
        ImageUtils.putImageIntoView(context, imageUri, imageView)
        container.addView(view)
        return view
    }
}
