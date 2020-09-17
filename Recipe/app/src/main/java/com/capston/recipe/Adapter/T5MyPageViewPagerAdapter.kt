package com.capston.recipe.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class T5MyPageViewPagerAdapter(manager:FragmentManager):FragmentPagerAdapter(manager){
    class FragmentInfo(val titleText: String, val fragment: Fragment)

//    private val fragmentList= arrayListOf<Fragment>()
    private val fragmentList = arrayListOf<FragmentInfo>()
    fun addFragment(title: String, fragment:Fragment){
        val info = FragmentInfo(title, fragment)
//        fragmentList.add(fragment)
        fragmentList.add(info)
    }
    override fun getItem(position: Int): Fragment {
        return fragmentList[position].fragment
    }
    override fun getCount(): Int {
        return fragmentList.size
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentList[position].titleText
    }

}