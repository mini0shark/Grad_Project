package com.capston.recipe.Adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Items.HistoryApiItem
import com.capston.recipe.R
import com.capston.recipe.Utils.ConstCodes
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.TimeUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.include_user_summary.view.*
import kotlinx.android.synthetic.main.recycler_t4_history_item.view.*

class T4HistoryItemsRecyclerViewAdapter(val context: Context, val activity: Activity, val fragment: Fragment,
                                        private val historyList:ArrayList<HistoryApiItem>):
    RecyclerView.Adapter<T4HistoryItemsRecyclerViewAdapter.ViewHolder>() {
    val TAG = "SearchPage Tag"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "createViewHolder")
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_t4_history_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, historyList[position])
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val layout: FrameLayout = view.frameLayout_recyclerHistoryItem
        private val userName: TextView =view.textView_recyclerHistoryItem_representUser

        val historyText: TextView =view.findViewById(R.id.textView_recyclerHistory_content)
        val historyPersonText: TextView =view.findViewById(R.id.textView_recyclerHistoryItem_person)

        private val backgroundImage: ImageView =view.findViewById(R.id.imageView_recyclerHistoryItem_recipeImage)

        private val time:TextView  = view.findViewById(R.id.textView_dateTime)
        private val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.bottom_nav_view)

        fun bind(context: Context, history: HistoryApiItem){
            // 음식이름으로보기 vs 제목으로 보기
            Log.i(TAG,"$history")
            when(history.classify){
                "RL"->{
                    layout.setOnClickListener {
                        var bundle= Bundle()
                        bundle.putInt("recipeId", history.obj!!.id)
                        if(history.obj!!.recipeTitle == null)
                            bundle.putInt("type", ConstCodes.STORY)
                        else
                            bundle.putInt("type", ConstCodes.RECIPE)
                        // RecipeDetail 상단
                        bottomNavigationView.visibility=View.GONE
                        fragment.findNavController().navigate(R.id.action_activityHistory_to_recipeDetail, bundle)
                    }
                    val title = if(history.obj!!.recipeTitle == null) "당신의 스토리"
                    else "레시피 \"${history.obj!!.recipeTitle}\""
                    historyText.text = "${title}(을)를 좋아합니다."
                }
                "RC"->{
                    // RecipeDetail Comment부분
                    layout.setOnClickListener {
                        var bundle= Bundle()
                        bundle.putInt("recipeId", history.obj!!.id)
                        if(history.obj!!.recipeTitle == null)
                            bundle.putInt("type", ConstCodes.STORY)
                        else
                            bundle.putInt("type", ConstCodes.RECIPE)
                        bundle.putInt("approachRoot", ConstCodes.COMMENT)
                        bottomNavigationView.visibility=View.GONE
                        fragment.findNavController().navigate(R.id.action_activityHistory_to_recipeDetail, bundle)
                    }
                    val title = if(history.obj!!.recipeTitle == null) "당신의 스토리"
                    else history.obj!!.recipeTitle
                    historyText.text = "\"$title\"에 댓글을 남겼습니다.\n" +
                            "${history.content}"
                }
                "RR"->{
                    layout.setOnClickListener{
                        var bundle= Bundle()
                        bundle.putInt("recipeId", history.obj!!.id)
                        bundle.putString("recipeName", history.obj!!.recipeTitle)
                        bottomNavigationView.visibility=View.GONE
                        fragment.findNavController().navigate(R.id.action_activityHistory_to_t4StoryList, bundle)
                    }
                    Log.i(TAG, "$history")
                    historyText.text = "\"${history.obj!!.recipeTitle}\"레시피로 요리를 만들었습니다.."
                }
                "UF"->{
                    layout.setOnClickListener{
                        val bundle = Bundle()
                        bundle.putInt("userId", history.user.id)
                        bottomNavigationView.visibility=View.GONE
                        fragment.findNavController().navigate(R.id.action_activityHistory_to_otherUserPage, bundle)
                    }
                    historyText.text = "팔로우를 시작했습니다."

                }
                "CL"->{
                    // Comment 부분으로 => 댓글창만 보이는 화면 따로...
                    layout.setOnClickListener {
                        var bundle= Bundle()
                        bundle.putInt("recipeId", history.obj!!.id)
                        if(history.obj!!.recipeTitle == null)
                            bundle.putInt("type", ConstCodes.STORY)
                        else
                            bundle.putInt("type", ConstCodes.RECIPE)
                        bundle.putInt("approachRoot", ConstCodes.COMMENT)
                        bottomNavigationView.visibility=View.GONE
                        fragment.findNavController().navigate(R.id.action_activityHistory_to_recipeDetail, bundle)
                    }
                    if(history.obj!!.recipeTitle != null) {
                        historyText.text = "\"${history.obj!!.recipeTitle}\"에 단 댓글을 좋아합니다.\n"
                        "${history.content}"
                    }
                    else{
                        historyText.text = "스토리에 단 댓글을 좋아합니다.\n" +
                                "${history.content}"
                    }
                }
            }

            TimeUtil.createdTimeToWhile(time, history.updateTime)
            if(history.user != null) {
                userName.text = history.user.nickname
            }
            else {
                userName.text = "탈퇴한 사용자"
            }
            if(history.count <= 1){
                historyPersonText.text = "님이"
            }else{
                historyPersonText.text = "님외 ${history.count}명이"
            }
            if(history.classify != "UF")
                ImageUtils.putImageIntoView(context, history.obj!!.multiImageResult, backgroundImage)
            else
                ImageUtils.putImageIntoView(context, history.user.profileImage, backgroundImage)
//            if(backgroundImage.drawable!=null) {
//                Log.i(TAG, "alpha -=> ${backgroundImage.drawable.alpha}")
//                backgroundImage.drawable.alpha = 70
//                Log.i(TAG, "alpha -=> ${backgroundImage.drawable.alpha}")
//            }

            //
//            image.setImageBitmap(ImageUtils.stringToImage(simpleItems.multiImageResult, context))
//            title.text = simpleItems.recipeTitle
//            name.text = simpleItems.foodName
//            likeCount.text = simpleItems.likesCount.toString()
        }
    }

}