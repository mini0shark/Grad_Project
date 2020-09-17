package com.capston.recipe.Adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Items.RecipeContainerSimpleApiItem
import com.capston.recipe.R
import com.capston.recipe.Utils.ConstCodes
import com.capston.recipe.Utils.ImageUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class T2SearchPageRecyclerViewAdapter(val context: Context, val activity: Activity, val fragment: Fragment,
                                      private val searchReferenceList:ArrayList<RecipeContainerSimpleApiItem>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    lateinit var bottomNav:BottomNavigationView
    val TAG = "SearchPage Tag"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        bottomNav = activity.findViewById(R.id.bottom_nav_view)
        val view:View?
        Log.i(TAG, "createView Holder ")
        return when(viewType){
            0-> {
                Log.i(TAG, "first creatView")
                view = LayoutInflater.from(context).inflate(R.layout.recycler_t2_search_item1, parent, false)
                FirstItemViewHolder(view)
            }
            1,3-> {
                Log.i(TAG, "second creatView")
                view = LayoutInflater.from(context).inflate(R.layout.recycler_t2_search_item2 , parent, false)
                SecondItemViewHolder(view)
            }
            2 -> {
                Log.i(TAG, "third creatView")
                view = LayoutInflater.from(context).inflate(R.layout.recycler_t2_search_item3, parent, false)
                ThirdItemViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position%4
    }
    override fun getItemCount(): Int {
        return searchReferenceList.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.i(TAG, "vindView Holder")
        when(position%4){
            0-> (holder as FirstItemViewHolder).bind(context, searchReferenceList[position])
            1,3 ->(holder as SecondItemViewHolder).bind(context, searchReferenceList[position])
            2->(holder as ThirdItemViewHolder).bind(context, searchReferenceList[position])
        }
    }

    inner class FirstItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val layout:LinearLayout = view?.findViewById(R.id.layout_searchItem1)
        val image: ImageView =view.findViewById(R.id.imageView_recyclerSearchItem1)
        val title: TextView =view.findViewById(R.id.textView_recyclerSearchItem1_title)
        val name: TextView =view.findViewById(R.id.textView_recyclerSearchItem1_foodName)
        val likeCount:TextView = view.findViewById(R.id.textView_likeCounter1)
        fun bind(context: Context, simpleItems:RecipeContainerSimpleApiItem){
            // 음식이름으로보기 vs 제목으로 보기
            Log.i(TAG, "firstImageView Holder")
            var bundle= Bundle()
            ImageUtils.putImageIntoView(context, simpleItems.multiImageResult, image)
            if(searchReferenceList[position].recipeTitle== null && searchReferenceList[position].foodName == null){
                bundle.putInt("type", ConstCodes.STORY)
                title.text = "STORY"
                name.text = ""
            }else{
                bundle.putInt("type", ConstCodes.RECIPE)
                title.text = simpleItems.recipeTitle
                name.text = simpleItems.foodName
            }
            likeCount.text = simpleItems.likesCount.toString()
            layout.setOnClickListener {
                bottomNav.visibility = View.GONE
                bundle.putInt("recipeId", simpleItems.id!!)
                fragment.findNavController().navigate(R.id.action_search_to_recipeDetail, bundle)
            }
        }
    }
    inner class SecondItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val layout:LinearLayout = view.findViewById(R.id.layout_searchItem2)
        val image: ImageView =view.findViewById(R.id.imageView_recyclerSearchItem2)
        val title: TextView =view.findViewById(R.id.textView_recyclerSearchItem2_title)
        val name: TextView =view.findViewById(R.id.textView_recyclerSearchItem2_foodName)
        val likeCount:TextView = view.findViewById(R.id.textView_likeCounter2)
        fun bind(context: Context, simpleItems:RecipeContainerSimpleApiItem){
            var bundle= Bundle()
            ImageUtils.putImageIntoView(context, simpleItems.multiImageResult, image)
            if(searchReferenceList[position].recipeTitle== null && searchReferenceList[position].foodName == null){
                bundle.putInt("type", ConstCodes.STORY)
                title.text = "STORY"
                name.text = ""
            }else{
                bundle.putInt("type", ConstCodes.RECIPE)
                title.text = simpleItems.recipeTitle
                name.text = simpleItems.foodName
            }
            layout.setOnClickListener {
                bundle.putInt("recipeId", simpleItems.id!!)
                bottomNav.visibility = View.GONE
                fragment.findNavController().navigate(R.id.action_search_to_recipeDetail, bundle)
            }
            likeCount.text = simpleItems.likesCount.toString()
        }
    }
    inner class ThirdItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val layout:LinearLayout = view.findViewById(R.id.layout_searchItem3)
        val image: ImageView =view.findViewById(R.id.imageView_recyclerSearchItem3)
        val title: TextView =view.findViewById(R.id.textView_recyclerSearchItem3_title)
        val name: TextView =view.findViewById(R.id.textView_recyclerSearchItem3_foodName)
        val likeCount:TextView = view.findViewById(R.id.textView_likeCounter3)
        fun bind(context: Context, simpleItems:RecipeContainerSimpleApiItem){
            var bundle= Bundle()
            // 음식이름으로보기 vs 제목으로 보기
            ImageUtils.putImageIntoView(context, simpleItems.multiImageResult, image)
            if(searchReferenceList[position].recipeTitle== null && searchReferenceList[position].foodName == null){
                bundle.putInt("type", ConstCodes.STORY)
                title.text = "STORY"
                name.text = ""
            }else{
                bundle.putInt("type", ConstCodes.RECIPE)
                title.text = simpleItems.recipeTitle
                name.text = simpleItems.foodName
            }
            layout.setOnClickListener {
                bundle.putInt("recipeId", simpleItems.id!!)
                bottomNav.visibility = View.GONE
                fragment.findNavController().navigate(R.id.action_search_to_recipeDetail, bundle)
            }
            likeCount.text = simpleItems.likesCount.toString()
        }
    }

}
