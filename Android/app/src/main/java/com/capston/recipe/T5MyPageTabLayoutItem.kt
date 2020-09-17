package com.capston.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Adapter.T5MyPageItemRecyclerViewAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.MyPageRecipeApiItem
import com.capston.recipe.Utils.ConstCodes.RECIPE
import com.capston.recipe.Utils.ConstCodes.STORY
import com.capston.recipe.Utils.ServerAddress
import kotlinx.android.synthetic.main.tab_recycler.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class T5MyPageTabLayoutItem(val userId:Int, val type: Int) : Fragment() {
    val TAG = "MyPageTabLayoutItem tag"
    lateinit var fragmentInflater:View
    lateinit var recipeRecyclerViewAdapter: T5MyPageItemRecyclerViewAdapter
    lateinit var itemList:ArrayList<MyPageRecipeApiItem>
    lateinit var storyRecyclerViewAdapter: T5MyPageItemRecyclerViewAdapter
    lateinit var recyclerView:RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentInflater = inflater.inflate(R.layout.tab_recycler, container, false)
        itemList = arrayListOf()
        when (type) {
            RECIPE -> {
                fragmentInflater.textView_tabRecycler.hint = "No Recipes"
                myRecipes()
            }
            STORY -> {
                fragmentInflater.textView_tabRecycler.hint = "No Stories"
                myStories()
            }
            else -> {
                fragmentInflater.textView_tabRecycler.hint = "No BookMark"
                myBookMarks()
            }
        }
        return fragmentInflater
    }

    private fun myStories(){
        storyRecyclerViewAdapter = T5MyPageItemRecyclerViewAdapter(context!!, this, itemList, false)
        recyclerView = fragmentInflater.recyclerView_tabLayout
        recyclerView.adapter = storyRecyclerViewAdapter
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.itemAnimator = DefaultItemAnimator()
        val recipeService = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        val call = recipeService.service.getMyPageItem(userId, "Story")

        call.enqueue(object : Callback<ArrayList<MyPageRecipeApiItem>> {
            override fun onResponse(call: Call<ArrayList<MyPageRecipeApiItem>>, response: Response<ArrayList<MyPageRecipeApiItem>>) {
                val responseBody = response.body()
                if(responseBody != null){
                    itemList.addAll(responseBody)
                    if(itemList.size>0)
                        fragmentInflater.textView_tabRecycler.visibility= View.GONE
                    storyRecyclerViewAdapter.notifyDataSetChanged()
                }else{
                    Log.i(TAG, "fail to receive recipeItems")
                }
            }

            override fun onFailure(call: Call<ArrayList<MyPageRecipeApiItem>>, t: Throwable) {
                Log.i(TAG, "fail to transmit")
                call.cancel()
                t.printStackTrace()
            }

        })
    }
    private fun myRecipes(){
        recipeRecyclerViewAdapter = T5MyPageItemRecyclerViewAdapter(context!!, this, itemList,true)
        recyclerView = fragmentInflater.recyclerView_tabLayout
        recyclerView.adapter = recipeRecyclerViewAdapter
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.itemAnimator = DefaultItemAnimator()
        val recipeService = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        val call = recipeService.service.getMyPageItem(userId, "Recipe")
        call.enqueue(object : Callback<ArrayList<MyPageRecipeApiItem>>{
            override fun onResponse(call: Call<ArrayList<MyPageRecipeApiItem>>, response: Response<ArrayList<MyPageRecipeApiItem>>) {
                val responseBody = response.body()
                Log.i(TAG, "resby : ")
                if(responseBody != null){
                    itemList.addAll(responseBody)
                    if(itemList.size>0)
                        fragmentInflater.textView_tabRecycler.visibility= View.GONE

                    recipeRecyclerViewAdapter.notifyDataSetChanged()
                }else{
                    Log.i(TAG, "fail to receive recipeItems")
                }
            }

            override fun onFailure(call: Call<ArrayList<MyPageRecipeApiItem>>, t: Throwable) {
                Log.i(TAG, "fail to transmit")
                call.cancel()
                t.printStackTrace()
            }

        })
    }
    private fun myBookMarks(){
        recipeRecyclerViewAdapter = T5MyPageItemRecyclerViewAdapter(context!!, this, itemList,true)
        recyclerView = fragmentInflater.recyclerView_tabLayout
        recyclerView.adapter = recipeRecyclerViewAdapter
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.itemAnimator = DefaultItemAnimator()
        val recipeService = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        val call = recipeService.service.getMyPageItem(userId, "BookMark")
        call.enqueue(object : Callback<ArrayList<MyPageRecipeApiItem>>{
            override fun onResponse(call: Call<ArrayList<MyPageRecipeApiItem>>, response: Response<ArrayList<MyPageRecipeApiItem>>) {
                val responseBody = response.body()
                Log.i(TAG, "resby : ")
                if(responseBody != null){
                    itemList.addAll(responseBody)
                    if(itemList.size>0)
                        fragmentInflater.textView_tabRecycler.visibility= View.GONE

                    recipeRecyclerViewAdapter.notifyDataSetChanged()
                }else{
                    Log.i(TAG, "fail to receive recipeItems")
                }
            }

            override fun onFailure(call: Call<ArrayList<MyPageRecipeApiItem>>, t: Throwable) {
                Log.i(TAG, "fail to transmit")
                call.cancel()
                t.printStackTrace()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "resume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "pause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "stop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "ondestView")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "ondest")
    }
}
