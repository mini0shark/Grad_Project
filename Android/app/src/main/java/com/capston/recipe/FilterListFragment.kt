package com.capston.recipe


import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Adapter.ThumbnailAdapter
import com.capston.recipe.MainActivity.Main.IMAGE_NAME
import com.capston.recipe.Utils.BitmapUtils
import com.capston.recipe.Utils.SpaceItemDecoration
import com.capston.recipe.interfaces.FilterListFragmentListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager

class FilterListFragment(val bitmap: Bitmap?) : BottomSheetDialogFragment(), FilterListFragmentListener {

    internal lateinit var recyclerView: RecyclerView
    internal var listener:FilterListFragmentListener?=null
    internal lateinit var adapter: ThumbnailAdapter
    internal lateinit var thumbnailItemList:MutableList<ThumbnailItem>


    companion object{
        internal var instance: FilterListFragment?=null

        fun getInstance(bitmap:Bitmap?): FilterListFragment {
            if(instance == null)
                instance = FilterListFragment(bitmap)
            return instance!!
        }
    }


    fun setListener(listFragmentListener:FilterListFragmentListener){
        this.listener=listFragmentListener
    }

    override fun onFilterSelected(filter: Filter) {
        if(listener !=null){
            listener!!.onFilterSelected(filter)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var fragmentView= inflater.inflate(R.layout.fragment_ta_filter_list, container, false)
        thumbnailItemList= arrayListOf()
        adapter = ThumbnailAdapter(activity!!, thumbnailItemList, this)

        recyclerView = fragmentView.findViewById(R.id.recyclerView_filter)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
            .toInt()
        recyclerView.addItemDecoration(SpaceItemDecoration(space))
        recyclerView.adapter= adapter
        displayImage(bitmap)
        return fragmentView
    }

    fun displayImage(bitmap: Bitmap?){
        val r = Runnable{
            val thumbImage: Bitmap = (if(bitmap == null)
                BitmapUtils.getBitmapFromAssets(activity!!, IMAGE_NAME, 100, 100)
            else
                Bitmap.createScaledBitmap(bitmap, 100, 100, false))
                ?: return@Runnable

            ThumbnailsManager.clearThumbs()
            if(thumbnailItemList!=null)
                thumbnailItemList.clear()

            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbImage
            thumbnailItem.filterName = "Normal"
            val filters = FilterPack.getFilterPack(activity!!)
            for(filter in filters){
                val item = ThumbnailItem()
                item.image = thumbImage
                item.filter = filter
                item.filterName = filter.name
                ThumbnailsManager.addThumb(item)
            }
            thumbnailItemList.addAll(ThumbnailsManager.processThumbs(activity))
            activity!!.runOnUiThread{
                adapter.notifyDataSetChanged()
            }

        }
        Thread(r).start()
    }
}
