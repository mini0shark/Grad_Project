package com.capston.recipe.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.capston.recipe.EditImageActivity
import com.capston.recipe.R
import com.capston.recipe.Utils.BitmapUtils
import com.capston.recipe.Utils.ConstCodes
import com.capston.recipe.Utils.ImageUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class T3ImageViewPagerRegisterAdapter(val context:Context, private val imageList:ArrayList<Bitmap>, val fragment: Fragment, private val imagePathList:ArrayList<String?>?): PagerAdapter() {
    override fun isViewFromObject(view: View, o: Any): Boolean {
        return (view == o)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    }

    override fun getCount(): Int {
        if( imagePathList!=null)
            if(imagePathList.size>0)
                return imagePathList.size
        return imageList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater:LayoutInflater= LayoutInflater.from(context)
        var view:View
        view = layoutInflater.inflate(R.layout.layout_ta_register_image, null)
        val imageView:ImageView = view.findViewById(R.id.imageView_image_item)
        if( imagePathList!=null) {
            if (imagePathList.size > 0) {
                ImageUtils.putImageIntoView(context, imagePathList[position], imageView)
            } else
                imageView.setImageBitmap(imageList[position])
        }else
            imageView.setImageBitmap(imageList[position])
        val imageButton = view.findViewById<ImageButton>(R.id.button_edit_item)
        imageButton.visibility=View.GONE
        container.addView(view)


        return view
    }
}