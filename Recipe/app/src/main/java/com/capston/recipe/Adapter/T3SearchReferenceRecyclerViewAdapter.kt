package com.capston.recipe.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Items.SearchReferenceApiItem
import com.capston.recipe.R
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.TreatNumber

class T3SearchReferenceRecyclerViewAdapter(val context: Context, val fragment:Fragment,
                                           val searchReferenceList:ArrayList<SearchReferenceApiItem>, val idList:ArrayList<Int>):
    RecyclerView.Adapter<T3SearchReferenceRecyclerViewAdapter.ViewHolder>(){
    private lateinit var listener:OnReferenceClick
    private var selected:Int=-1
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val title =view.findViewById<TextView>(R.id.textView_searchReferenceRecyclerItem_title)
        val foodName=view.findViewById<TextView>(R.id.textView_searchReferenceRecyclerItem_foodName)
        val image = view.findViewById<ImageView>(R.id.imageView_searchReferenceImage)
        val writer = view.findViewById<TextView>(R.id.textView_recyclerSearchReferenceItem_writer)
        val likeCount= view.findViewById<TextView>(R.id.textView_recyclerSearchReferenceItem_likeCount)
        val layout = view.findViewById<LinearLayout>(R.id.layout_recyclerSearchReferenceItem)
        val v = view
        fun bind(searchItem:SearchReferenceApiItem, context:Context){
            title.text = searchItem.recipeTitle
            foodName.text = searchItem.foodName

            ImageUtils.putImageIntoView(context, searchItem.multiImageResult, image)
            writer.text = searchItem.user.nickname
            likeCount.text = TreatNumber.convertBigNumber(searchItem.likesCount)
        }
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
//    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(searchReferenceList[position], context)
    when {
        selected == position -> {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
        searchReferenceList[position].id in idList -> {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.filter_label_normal))
        }
        else -> {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.invisible))
        }
    }
        holder.v.setOnClickListener{
            if(searchReferenceList[position].id !in idList) {
                selected = position
                notifyDataSetChanged()
                listener.onItemClick(searchReferenceList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_t23_search_reference_item, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return searchReferenceList.size
    }

    fun setOnItemClickListener(listener:OnReferenceClick){
        this.listener = listener
    }

    interface OnReferenceClick{
        fun onItemClick(reference:SearchReferenceApiItem)
    }

}