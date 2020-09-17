package com.capston.recipe.Adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Items.FollowUserApiItem
import com.capston.recipe.R
import com.capston.recipe.Utils.ImageUtils

class TAFollowListRecyclerAdapter (val context: Context,  val fragment: Fragment,
                                   private val followUserItems:ArrayList<FollowUserApiItem>):
    RecyclerView.Adapter<TAFollowListRecyclerAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_ta_follow_item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return followUserItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, followUserItems[position])
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view?.findViewById(R.id.layout_recyclerSearchItemUser)
        val image: ImageView =view.findViewById(R.id.imageView_followItem_profile)
        val nickname: TextView =view.findViewById(R.id.textView_followItem_nickname)
        val followTime: TextView =view.findViewById(R.id.textView_followItem_followTime)
        fun bind(context: Context, followUser:FollowUserApiItem){
            layout.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("userId", followUser.id)
                fragment.findNavController().navigate(R.id.action_followList_to_otherUserPage, bundle)
            }
            ImageUtils.putImageIntoView(context, followUser.profileImage, image)
            nickname.text = followUser.nickname
            followTime.text = followUser.followTime
        }

    }
}
