package com.capston.recipe.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Items.AddImageItem
import com.capston.recipe.EditImageActivity
import com.capston.recipe.R
import com.capston.recipe.Utils.ConstCodes.EDIT_REQUEST_CODE

class TAAddImageRecyclerViewAdapterForStory(val context: Context, val fragment: Fragment, val addImageList:ArrayList<AddImageItem>):
    RecyclerView.Adapter<TAAddImageRecyclerViewAdapterForStory.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_ta_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return addImageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bind(addImageList[position], context)
    }


    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val imageView = view?.findViewById<ImageView>(R.id.imageView_image_item)
        val imageButton = view?.findViewById<ImageButton>(R.id.button_edit_item)
        fun bind(addImageItem: AddImageItem, context: Context){
            imageView.setImageURI(addImageItem.uri)
            imageView.adjustViewBounds = true
            imageButton.setOnClickListener {
                //여기서 이미지 편집이 되게
                val intent = Intent(context, EditImageActivity::class.java)
//                val str = ImageUtils.bitmapToString(addImageItem.bitmap)
                intent.putExtra("order",addImageItem.order)
                intent.putExtra("editImage", addImageItem.uri)
//                Log.i(TAG, "len : ${str.length}, \n$str ")


                fragment.startActivityForResult(intent, EDIT_REQUEST_CODE)
            }
        }
    }

}