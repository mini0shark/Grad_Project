package com.capston.recipe.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Items.MyPageRecipeApiItem
import com.capston.recipe.R
import com.capston.recipe.Utils.ConstCodes
import com.capston.recipe.Utils.ImageUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception

class T5MyPageItemRecyclerViewAdapter (val context: Context, val fragment: Fragment,
                                       private val myPageItemList:ArrayList<MyPageRecipeApiItem>, val type:Boolean):
    RecyclerView.Adapter<T5MyPageItemRecyclerViewAdapter.ViewHolder>(){
    val TAG = "MyPageRecycler tag"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_t5_grid_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myPageItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bind(myPageItemList[position], context)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val rootLayout =  view.findViewById<LinearLayout>(R.id.layout_layoutRecyclerView_grid)
        private val imageView = view.findViewById<ImageView>(R.id.imageView_recyclerView_gridImage)
        private val multiImageView = view.findViewById<ImageView>(R.id.imageView_recyclerView_multi)
        private val bottomNavigationView: BottomNavigationView = fragment.activity!!.findViewById(R.id.bottom_nav_view)
        fun bind(recipeContainer:MyPageRecipeApiItem, context:Context){
            ImageUtils.putImageIntoView(context, recipeContainer.multiImageResult, imageView)
            if(recipeContainer.imageCount <= 1){
                multiImageView.visibility = View.GONE
            }
            rootLayout.setOnClickListener {
                var bundle= Bundle()
                bundle.putInt("recipeId", recipeContainer.id!!)
                bottomNavigationView.visibility=View.GONE
                //patchHitCount(homePageItem.id)        //*********************************************나중에 꼭
                if(type){   //Recipe
                    bundle.putInt("type", ConstCodes.RECIPE)
                }else{      // Story
                    bundle.putInt("type", ConstCodes.STORY)
                }
                try {
                    fragment.findNavController().navigate(R.id.action_myPage_to_recipeDetail, bundle)
                }catch (e:Exception){
                    fragment.findNavController().navigate(R.id.action_otherUserPage_to_recipeDetail, bundle)
                }

            }
        }
    }


}