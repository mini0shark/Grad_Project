package com.capston.recipe.Adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Items.SearchResultRecipeApiItem
import com.capston.recipe.Items.SearchResultStoryApiItem
import com.capston.recipe.Items.SearchResultUserApiItem
import com.capston.recipe.R
import com.capston.recipe.T2SearchDetail
import com.capston.recipe.Utils.ConstCodes
import com.capston.recipe.Utils.ImageUtils
import kotlinx.android.synthetic.main.recycler_t2_search_item_recipe.view.*
import kotlinx.android.synthetic.main.recycler_t2_search_item_story.view.*
import kotlinx.android.synthetic.main.recycler_t2_search_item_user.view.*

class T2SearchItemRecyclerAdapter (val context: Context, val fragment: Fragment,
                                   val recipeList:ArrayList<SearchResultRecipeApiItem>?=null,
                                   val storyList:ArrayList<SearchResultStoryApiItem>?=null,
                                   val accountList:ArrayList<SearchResultUserApiItem>?=null,
                                   val type:Int):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val TAG = "searchRcyAdp tag"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view:View?
        Log.i(TAG, "$type")
        return when(type) {
            ConstCodes.RECIPE -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_t2_search_item_recipe, parent, false)
                RecipeViewHolder(view)
            }
            ConstCodes.STORY-> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_t2_search_item_story, parent, false)
                StoryViewHolder(view)
            }
            ConstCodes.ACCOUNT-> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_t2_search_item_user, parent, false)
                AccountViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun getItemCount(): Int {
        Log.i(TAG, "$type")
        Log.i(TAG, "re $recipeList")
        Log.i(TAG, "st $storyList")
        Log.i(TAG, "ac $accountList")
        return when(type){
            ConstCodes.RECIPE->{
                recipeList!!.size
            }
            ConstCodes.STORY->{
                storyList!!.size
            }
            ConstCodes.ACCOUNT->{
                accountList!!.size
            }
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(type){
            ConstCodes.RECIPE->{
                (holder as RecipeViewHolder).bind(recipeList!![position])
            }
            ConstCodes.STORY->{
                (holder as StoryViewHolder).bind(storyList!![position])
            }
            ConstCodes.ACCOUNT->{
                (holder as AccountViewHolder).bind(accountList!![position])
            }
        }
    }
    inner class RecipeViewHolder(view: View):RecyclerView.ViewHolder(view)  {
        val image = view.imageView_searchItemRecipe
        val title = view.textView_searchItemRecipe_title
        val foodName = view.textView_searchItemRecipe_foodName
        val copied = view.textView_recyclerSearchItemRecipe_copied
        val likeCount = view.textView_recyclerSearchItemRecipe_likeCount
        val layout = view.layout_recyclerSearchItemRecipe

        fun bind(searchItem: SearchResultRecipeApiItem){
            ImageUtils.putImageIntoView(context, searchItem.multiImageResult, image)
            Log.i("viewHolder tag", "${searchItem.title}")
            title.text = searchItem.title
            foodName.text = searchItem.foodName
            copied.text = searchItem.copiedCount.toString()
            likeCount.text = searchItem.likesCount.toString()
            layout.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("type", ConstCodes.RECIPE)
                bundle.putInt("recipeId", searchItem.id)
//                fragment.findNavController().navigate(R.id.action_searchDetail_to_recipeDetail, bundle)
                (fragment as T2SearchDetail).move(bundle)
            }
        }
    }
    inner class StoryViewHolder(view: View):RecyclerView.ViewHolder(view)  {
        val image = view.imageView_searchStoryItem
        val introText = view.textView_searchItemStory_introduce
        val likeCount = view.textView_recyclerSearchItemStory_likeCount
        val layout = view.layout_recyclerSearchItemStory
        fun bind(searchItem: SearchResultStoryApiItem){
            ImageUtils.putImageIntoView(context, searchItem.multiImageResult, image)
            introText.text = searchItem.introduceRecipe
            likeCount.text = searchItem.likesCount.toString()
            layout.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("type", ConstCodes.STORY)
                bundle.putInt("recipeId", searchItem.id)
//                fragment.findNavController().navigate(R.id.action_searchDetail_to_recipeDetail, bundle)
                (fragment as T2SearchDetail).move(bundle)
            }
        }

    }
    inner class AccountViewHolder(view: View):RecyclerView.ViewHolder(view)  {
        val image = view.imageView_searchUserItem_profile
        val nickname = view.textView_recyclerSearchItemUser_nickname
        val postCount = view.textView_recyclerSearchItemUser_postCount
        val followCount = view.textView_recyclerSearchItemUser_followCount
        val layout = view.layout_recyclerSearchItemUser
        fun bind(accountItem: SearchResultUserApiItem){
            ImageUtils.putImageIntoCircleView(context, accountItem.profileImage, image)
            nickname.text = accountItem.nickname
            postCount.text = accountItem.postCount.toString()
            followCount.text = accountItem.followerCount.toString()

            layout.setOnClickListener {
                val bundle=Bundle()
                bundle.putInt("userId", accountItem.id!!)
                fragment.findNavController().navigate(R.id.action_searchDetail_to_otherUserPage, bundle)

            }
        }

    }

}