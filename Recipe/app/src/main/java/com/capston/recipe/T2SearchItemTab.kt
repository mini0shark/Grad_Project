package com.capston.recipe

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Adapter.T2SearchItemRecyclerAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Api.userApi.UserService
import com.capston.recipe.Items.SearchResultRecipeApiItem
import com.capston.recipe.Items.SearchResultStoryApiItem
import com.capston.recipe.Items.SearchResultUserApiItem
import com.capston.recipe.Utils.ServerAddress
import kotlinx.android.synthetic.main.fragment_t2_search_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class T2SearchItemTab(
    val detailFragment:Fragment,
    val recipeList:ArrayList<SearchResultRecipeApiItem>,
    val storyList:ArrayList<SearchResultStoryApiItem>,
    val accountList:ArrayList<SearchResultUserApiItem>,
    val type:Int) : Fragment() {
    val TAG ="T2SearchItem"
    //    private lateinit var listener: TextSubmitListener
    lateinit var fragmentInflater:View
    private lateinit var mAdapter:T2SearchItemRecyclerAdapter
    private lateinit var recyclerView:RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        commonCommand()
        fragmentInflater = inflater.inflate(R.layout.fragment_t2_search_item, container, false)
        Log.i(TAG, "tab created $type")

        setAdapter(recipeList, storyList, accountList)
        return fragmentInflater
    }
    fun setAdapter(recipeList:ArrayList<SearchResultRecipeApiItem>?
                   , storyList:ArrayList<SearchResultStoryApiItem>?
                   , accountList:ArrayList<SearchResultUserApiItem>?){
        recyclerView = fragmentInflater.recyclerView_searchItem
        recyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL,false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter= T2SearchItemRecyclerAdapter(context!!, detailFragment, recipeList, storyList, accountList, type)
        recyclerView.adapter = mAdapter
    }

    fun recipeCodes(searchQuery:String?, detActivity:Activity, detContext: Context) {
        if(searchQuery !=null) {
//            if (activity != null) {
            val service =
                RecipeService(
                    detActivity!!,
                    detContext!!, ServerAddress.serverAddress).service
            val call = service.getRecipeSearchWithQuery(searchQuery)
            call.enqueue(object : Callback<ArrayList<SearchResultRecipeApiItem>> {
                override fun onResponse(
                    call: Call<ArrayList<SearchResultRecipeApiItem>>,
                    response: Response<ArrayList<SearchResultRecipeApiItem>>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        for (a in responseBody)
                            Log.i(TAG, "$a")
                        clearLists()
                        recipeList!!.addAll(responseBody)
                    }

                    mAdapter.notifyDataSetChanged()
                }

                override fun onFailure(
                    call: Call<ArrayList<SearchResultRecipeApiItem>>,
                    t: Throwable
                ) {
                    call.cancel()
                    Log.e(TAG, "fail to call RecipeQuery")
                }

            })
        }
    }

    private fun clearLists() {
        recipeList.clear()
        storyList.clear()
        accountList.clear()
    }


    fun storyCodes(searchQuery:String?, detActivity:Activity, detContext: Context) {
        if(searchQuery !=null) {
            Log.i(TAG, "activity : $activity")
            val service = RecipeService(
                detActivity!!
                , detContext!!
                , ServerAddress.serverAddress
            ).service
            val call = service.getStorySearchWithQuery(searchQuery)
            call.enqueue(object : Callback<ArrayList<SearchResultStoryApiItem>> {
                override fun onResponse(
                    call: Call<ArrayList<SearchResultStoryApiItem>>,
                    response: Response<ArrayList<SearchResultStoryApiItem>>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        for (a in responseBody)
                            Log.i(TAG, "$a")
                        clearLists()
                        storyList!!.addAll(responseBody)
                    }
                    mAdapter.notifyDataSetChanged()
                }

                override fun onFailure(
                    call: Call<ArrayList<SearchResultStoryApiItem>>,
                    t: Throwable
                ) {
                    call.cancel()
                    Log.e(TAG, "fail to call StoryQuery")
                }

            })
        }
    }

    fun accountCodes(searchQuery:String?, detActivity:Activity, detContext: Context) {
        if(searchQuery !=null) {
            val service = UserService(detActivity!!,
                detContext!!, ServerAddress.serverAddress).service
            val query = searchQuery ?: ""
            val call = service.getSimpleUserForSearch(query)
            call.enqueue(object : Callback<ArrayList<SearchResultUserApiItem>> {
                override fun onResponse(
                    call: Call<ArrayList<SearchResultUserApiItem>>,
                    response: Response<ArrayList<SearchResultUserApiItem>>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        for (a in responseBody)
                            Log.i(TAG, "$a")
                        clearLists()
                        accountList!!.addAll(responseBody)
                    }
                    mAdapter.notifyDataSetChanged()
                }

                override fun onFailure(
                    call: Call<ArrayList<SearchResultUserApiItem>>,
                    t: Throwable
                ) {
                    call.cancel()
                    Log.e(TAG, "fail to call AccountQuery")
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
