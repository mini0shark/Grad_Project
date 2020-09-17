package com.capston.recipe.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.capston.recipe.R

class T3SearchReferenceViewPagerAdapter(val context:Context, manager:FragmentManager): FragmentPagerAdapter(manager){
    class FragmentInfo(val titleText: String, val fragment: Fragment, val layoutId:Int)

    //    private val fragmentList= arrayListOf<Fragment>()
    private val fragmentList = arrayListOf<FragmentInfo>()
    fun addFragment(title: String, fragment: Fragment, layoutId:Int){
        val info = FragmentInfo(title, fragment, layoutId)
//        fragmentList.add(fragment)
        fragmentList.add(info)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position].fragment
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return (view == o)
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentList[position].titleText
    }
//
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(fragmentList[position].layoutId, null)
        when(position){
            0->{

            }
            else->{

            }
        }
        container.addView(view)
        return view
    }

}