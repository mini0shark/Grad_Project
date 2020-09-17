package com.capston.recipe.Adapter

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
import com.capston.recipe.Items.StoryHistoryApiItem
import com.capston.recipe.R
import com.capston.recipe.Utils.ConstCodes.STORY
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.TimeUtil

class StoryHistoryApiItemRecyclerView  (val context: Context, val fragment: Fragment,
                                        private val followUserItems:ArrayList<StoryHistoryApiItem>):
    RecyclerView.Adapter<StoryHistoryApiItemRecyclerView.ViewHolder>(){
    val TAG = "StoryHistList tag"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_t4_copied_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return followUserItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, followUserItems[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view?.findViewById(R.id.layout_recyclerCopiedItem)
        val image: ImageView =view.findViewById(R.id.imageView_recyclerHistoryItem_recipeImage)      //background
        val nickname: TextView =view.findViewById(R.id.textView_recyclerCopiedItem_writer)
        val introSummary:TextView= view.findViewById(R.id.textView_recyclerHistory_introduceSummary)
        val storyTime: TextView =view.findViewById(R.id.textView_recyclerCopiedItem_dateTime)
        fun bind(context: Context, storyItems: StoryHistoryApiItem){
            layout.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("recipeId", storyItems.id)
                bundle.putInt("type", STORY)
                fragment.findNavController().navigate(R.id.action_t4StoryList_to_recipeDetail, bundle)
            }
            ImageUtils.putImageIntoView(context, storyItems.multiImageResult, image)
            if(storyItems.user!=null)
                nickname.text = storyItems.user!!.nickname
            else
                nickname.text = "탈퇴한 사용자"
            introSummary.text = storyItems.introduce

            TimeUtil.createdTimeToWhile(storyTime, storyItems.updateTime)

        }

    }
}