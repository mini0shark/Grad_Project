package com.capston.recipe.Adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.capston.recipe.T2SearchItemTab

class T2SearchDetailPageViewPagerAdapter(manager:FragmentManager):FragmentPagerAdapter(manager){
    class FragmentInfo(val titleText: String, val fragment: T2SearchItemTab)

//    private val fragmentList= arrayListOf<Fragment>()
    private val fragmentList = arrayListOf<FragmentInfo>()
    fun addFragment(title: String, fragment:T2SearchItemTab){
        val info = FragmentInfo(title, fragment)
//        fragmentList.add(fragment)
        fragmentList.add(info)
    }
    fun deleteFragment(){
        fragmentList.clear()
    }
    override fun getItem(position: Int): T2SearchItemTab {
        return fragmentList[position].fragment
    }
    override fun getCount(): Int {
        return fragmentList.size
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentList[position].titleText
    }

}