package com.capston.recipe

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.capston.recipe.Adapter.T2SearchDetailPageViewPagerAdapter
import com.capston.recipe.Items.SearchResultRecipeApiItem
import com.capston.recipe.Items.SearchResultStoryApiItem
import com.capston.recipe.Items.SearchResultUserApiItem
import com.capston.recipe.Utils.ConstCodes.ACCOUNT
import com.capston.recipe.Utils.ConstCodes.RECIPE
import com.capston.recipe.Utils.ConstCodes.STORY
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_t2_search_detail.view.*

class T2SearchDetail : Fragment() {
    val TAG = "T2SearchDetail"
    lateinit var tabLayout:TabLayout
    lateinit var viewPager: ViewPager
    lateinit var fragmentAdapter: T2SearchDetailPageViewPagerAdapter
    lateinit var fragmentArr:ArrayList<T2SearchItemTab>
    lateinit var fragmentInflater:View
    lateinit var loadAct:Activity
    lateinit var loadctx:Context

    val recipeList:ArrayList<SearchResultRecipeApiItem> = arrayListOf()
    val storyList:ArrayList<SearchResultStoryApiItem> = arrayListOf()
    val accountList:ArrayList<SearchResultUserApiItem> = arrayListOf()
     var searchView: SearchView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentInflater = inflater.inflate(R.layout.fragment_t2_search_detail, container, false)
        loadAct=activity!!
        loadctx=context!!

        return fragmentInflater
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu_search_fragment, menu)
        searchView= menu.findItem(R.id.menuItem_search_fragment).actionView as SearchView
        searchView!!.maxWidth = Int.MAX_VALUE
        searchView!!.queryHint = "Recipe를 검색해 보세요!!"
        searchView!!.setIconifiedByDefault(false)
        searchView!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                textSubmitListener(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun textSubmitListener(query:String?) {
        recipeList.clear()
        storyList.clear()
        accountList.clear()
        if(query!=null)
            when(viewPager.currentItem){
                0->{
                    fragmentAdapter.getItem(viewPager.currentItem).recipeCodes(query, activity!!, context!!)
                }
                1->{
                    fragmentAdapter.getItem(viewPager.currentItem).storyCodes(query,activity!!, context!!)
                }
                2->{
                    fragmentAdapter.getItem(viewPager.currentItem).accountCodes(query, activity!!, context!!)
                }
            }
    }

    private fun hideKeyboard(){
        val inputMethodManager= activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
    fun move(bundle:Bundle){
        this.findNavController().navigate(R.id.action_searchDetail_to_recipeDetail, bundle)
    }


    private fun tabLayoutSetting(){
        fragmentArr = arrayListOf()
        fragmentArr.add(T2SearchItemTab(this, recipeList, storyList, accountList, RECIPE))   // 레시피
        Log.i(TAG, "added_recipe")
        fragmentArr.add(T2SearchItemTab(this, recipeList, storyList, accountList, STORY))   // 스토리
        Log.i(TAG, "added_str")
        fragmentArr.add(T2SearchItemTab(this, recipeList, storyList, accountList, ACCOUNT))   // 계정
        Log.i(TAG, "added_acc")
//        fragmentArr.add()  // 장소
        fragmentAdapter = T2SearchDetailPageViewPagerAdapter(fragmentManager!!)
        tabLayout = fragmentInflater.tabLayout_searchDetail
        viewPager = fragmentInflater.viewPager_searchDetail
        fragmentAdapter.addFragment("레시피", fragmentArr[0])
        fragmentAdapter.addFragment("스토리", fragmentArr[1])
        fragmentAdapter.addFragment("계정", fragmentArr[2])
        viewPager.adapter = fragmentAdapter

        viewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                tabIconSelect()
                if(searchView!=null) {
                    val q = searchView!!.query?.toString()
                    if(!q.isNullOrEmpty())
                        textSubmitListener(q)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) { }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })
        tabLayout.setupWithViewPager(viewPager)
        tabIconSelect()
    }
    fun tabIconSelect() {
        for (i in 0 until fragmentArr.size) {
            val tab = tabLayout.getTabAt(i)!!
            if (tab.isSelected) {
                // TabBtnText.get(i).setTextColor(resources.getColor(R.color.point))
            } else {
                // TabBtnText.get(i).setTextColor(resources.getColor(R.color.lightGray))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        Log.i(TAG, "res  act : $activity, ctx : $context")
        tabLayoutSetting()
        while(findNavController().currentDestination!!.id!=R.id.searchDetail)
            findNavController().popBackStack()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "ondestView")
        val trans = fragmentManager!!.beginTransaction()
        for(frag in fragmentArr)
            trans.remove(frag)
        trans.commit()
    }

}
