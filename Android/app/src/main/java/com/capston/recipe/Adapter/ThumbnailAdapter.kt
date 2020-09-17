package com.capston.recipe.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.interfaces.FilterListFragmentListener
import com.capston.recipe.R
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.android.synthetic.main.thumnail_list_item.view.*

class ThumbnailAdapter (private val context: Context,
                        private val thumnailItemList:List<ThumbnailItem>,
                        private val listener: FilterListFragmentListener
): RecyclerView.Adapter<ThumbnailAdapter.MyViewHolder>() {

    private var selectedIndex=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(context).inflate(R.layout.thumnail_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return thumnailItemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val thumbNailItem=thumnailItemList[position]
        holder.thumNail.setImageBitmap(thumbNailItem.image)
        holder.thumNail.setOnClickListener {
            listener.onFilterSelected(thumbNailItem.filter)
            selectedIndex = position
            notifyDataSetChanged()
        }
        holder.filterName.text=thumbNailItem.filterName

        if(selectedIndex == position)
            holder.filterName.setTextColor(ContextCompat.getColor(context, R.color.filter_label_selected))
        else
            holder.filterName.setTextColor(ContextCompat.getColor(context, R.color.filter_label_normal))
    }
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var thumNail:ImageView
        var filterName:TextView
        init{
            thumNail=itemView.thumbnail
            filterName = itemView.filter_name
        }
    }
}