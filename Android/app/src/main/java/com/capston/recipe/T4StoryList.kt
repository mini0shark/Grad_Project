package com.capston.recipe


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Adapter.StoryHistoryApiItemRecyclerView
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.StoryHistoryApiItem
import com.capston.recipe.Utils.ConstCodes.RECIPE
import com.capston.recipe.Utils.ServerAddress
import kotlinx.android.synthetic.main.fragment_t4_story_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class T4StoryList : Fragment() {
    val TAG = "T4StoryList"
    lateinit var fragmentInflater:View
    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: StoryHistoryApiItemRecyclerView
    val storyList = arrayListOf<StoryHistoryApiItem>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        fragmentInflater = inflater.inflate(R.layout.fragment_t4_story_list, container, false)
        val recipeName = arguments?.getString("recipeName")!!
        (activity as AppCompatActivity).supportActionBar?.title = "\"${recipeName}\"를 요리한 스토리"
        val recipeId = arguments?.getInt("recipeId")!!
        fragmentInflater.button_storyList_goToMyRecipe.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("recipeId", recipeId)
            bundle.putInt("type", RECIPE)
            findNavController().navigate(R.id.action_t4StoryList_to_recipeDetail, bundle)
        }
        adapterSetting()
        getStoryList()
        return fragmentInflater
    }
    fun adapterSetting(){
        recyclerView = fragmentInflater.recyclerView_storyList
        mAdapter = StoryHistoryApiItemRecyclerView(context!!, this, storyList)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL,false)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }
    fun getStoryList(){
        val recipeId = arguments?.getInt("recipeId")!!
        val service = RecipeService(activity!!, context!!, ServerAddress.serverAddress).service
        val call = service.getCopiedList(recipeId)
        call.enqueue(object : Callback<ArrayList<StoryHistoryApiItem>>{
            override fun onResponse(call: Call<ArrayList<StoryHistoryApiItem>>, response: Response<ArrayList<StoryHistoryApiItem>>) {
                val responseBody = response.body()
                if(responseBody!=null){
                    storyList.clear()
                    storyList.addAll(responseBody)
                    mAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ArrayList<StoryHistoryApiItem>>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Log.i(TAG, "fail to get StoryList")
            }

        })
    }


}
