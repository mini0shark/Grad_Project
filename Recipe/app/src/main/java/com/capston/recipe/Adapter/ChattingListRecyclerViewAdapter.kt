package com.capston.recipe.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Items.ChattingListApiItem
import com.capston.recipe.R

class ChattingListRecyclerViewAdapter   (val context: Context, val fragment: Fragment,
                                         private val chattingItems:ArrayList<ChattingListApiItem>):
    RecyclerView.Adapter<ChattingListRecyclerViewAdapter.ViewHolder>(){
    val TAG = "ChattingList tag"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_t4_copied_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chattingItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, chattingItems[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(context: Context, chattingItem: ChattingListApiItem){

        }

    }
}