package com.capston.recipe.Adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.HompageApiItem
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.R
import com.capston.recipe.Utils.ConstCodes
import com.capston.recipe.Utils.ConstCodes.COMMENT
import com.capston.recipe.Utils.ConstCodes.FALSE
import com.capston.recipe.Utils.ConstCodes.TRUE
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.ServerAddress
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class T1HomeRecyclerViewAdapter(val context: Context, val activity: Activity,
                                val addImageList:ArrayList<HompageApiItem>,
                                val user:UserApiItem,
                                val fragment: Fragment):
    RecyclerView.Adapter<T1HomeRecyclerViewAdapter.ViewHolder>() {
    val TAG = "HomeRecyclerViewAdapter"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_t1_home_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return addImageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(addImageList[position], context)
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val userProfileImageView: ImageView = view.findViewById(R.id.imageView_homeItem_userImage)
        private val userNameTextView: TextView = view.findViewById(R.id.textView_homeItem_userName)
        private val userLayout:LinearLayout= view.findViewById(R.id.layout_homeItem_user)

        private val viewPager: ViewPager = view.findViewById(R.id.viewPager_homeItem)
        private val contentText: TextView = view.findViewById(R.id.textView_homeItemContent)
        private val likeButton: ImageButton = view.findViewById(R.id.button_homeItem_like)
        private val likeAmountTextView: TextView = view.findViewById(R.id.textView_homeItem_likeAmount)
        private val commentButton: ImageButton = view.findViewById(R.id.button_homeItem_comment)
        private val commentTextView: TextView = view.findViewById(R.id.textView_homeItem_commentAmount)
        private val addListButton: ImageButton = view.findViewById(R.id.button_homeItem_addToMyList)
        private val goToRecipeDetailButton: ImageButton = view.findViewById(R.id.button_homeItem_goToRecipe)
        private val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.bottom_nav_view)
        private val titleTextView: TextView = view.findViewById(R.id.textView_homeItem_title)
        private val typeTextView: TextView = view.findViewById(R.id.textView_homeItem_type)
        private val layoutOnlyRecipeText:LinearLayout = view.findViewById(R.id.linearLayout_recyclerHome_onlyRecipe)
        private val hitCountTextView: TextView = view.findViewById(R.id.textView_homeItem_hitCount)
        private val layoutPage:LinearLayout = view.findViewById(R.id.layout_homeItem)
        fun bind(recipeDetailItem: HompageApiItem, context: Context){
            val title = recipeDetailItem.recipeTitle


            titleTextView.text = title
            if(recipeDetailItem.user != null) {
                Log.e(TAG, "${recipeDetailItem.user.profileImage}")
                ImageUtils.putImageIntoCircleView(context,recipeDetailItem.user.profileImage, userProfileImageView)
//                userProfileImageView.setImageBitmap(ImageUtils.stringToImage(recipeDetailItem.user.profileImage, context))
                userNameTextView.text = recipeDetailItem.user.nickname
                userLayout.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putInt("userId", recipeDetailItem.user.id)
                    bottomNavigationView.visibility= GONE
                    fragment.findNavController().navigate(R.id.action_home_to_otherUserPage, bundle)
                }
            }else{
                userNameTextView.text = "탈퇴한 사용자 입니다."
                ImageUtils.putImageIntoCircleView(context,ContextCompat.getDrawable(context, R.drawable.basic_profile_image)!!, userProfileImageView)

            }


            // -- 스토리 vs 레시피
            Log.i(TAG, "type : ${recipeDetailItem.type}")
            var bundle= Bundle()
            bundle.putInt("recipeId", recipeDetailItem.id!!)
            if(!recipeDetailItem.type){     // 스토리
                typeTextView.hint = "FOOD STORY"
                bundle.putInt("type", ConstCodes.STORY)
                layoutOnlyRecipeText.visibility = GONE
            }else{              // 레시피
                typeTextView.hint = "TITLE"
                layoutOnlyRecipeText.visibility = VISIBLE
                bundle.putInt("type", ConstCodes.RECIPE)
            }
            hitCountTextView.text = recipeDetailItem.hitCount.toString()    //조회수 보이기
            goToRecipeDetailButton.setOnClickListener {
                //patchHitCount(homePageItem.id)        //*********************************************나중에 꼭
                Log.i(TAG, "hitCount : ${recipeDetailItem.hitCount}, ${hitCountTextView.visibility}")
                bottomNavigationView.visibility= GONE
                fragment.findNavController().navigate(R.id.action_home_to_recipeDetail, bundle)
            }
            // 스토리 vs 레시피 --

            //--이미지 세팅
            val imageList= arrayListOf<String?>()
            if(recipeDetailItem.multiImageResult!=null) {
                for (contentImage in recipeDetailItem.multiImageResult!!) {
                    imageList.add(contentImage.image)
                }
            }else{
                imageList.add(null)
            }
            Log.i(TAG, "multiImageSize : ${imageList.size}")
            viewPager.clipToPadding = false
            if(recipeDetailItem.multiImageResult!=null) {
                viewPager.adapter = T3ImageViewPagerAdapter(context, imageList)
            }
            contentText.text = recipeDetailItem.introduceRecipe
            //이미지 세팅--

            // -- like 설정
            var likeFlag = false
//            Log.i(TAG, "likes : ${homePageItem.likes}")
            ////////////////////////////// 실험!!!!!!!!!!!!!!!!!!!!!!!!!
            for(like in recipeDetailItem.likes){
                Log.i(TAG, "item : ${like.userId}, user.id : ${user.id}")
                if(like.userId == user.id)
                    likeFlag = true
            }
            if(likeFlag){
                likeButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_pushed_24dp))
                likeButton.tag = TRUE
            }else{
                likeButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_black_24dp))
                likeButton.tag = FALSE
            }
            likeAmountTextView.text = recipeDetailItem.likes.size.toString()
            likeButton.setOnClickListener {
                var flag = likeButton.tag == FALSE
                if(flag){
                    likeButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_pushed_24dp))
                    likeButton.tag = TRUE
                }else{
                    likeButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_black_24dp))
                    likeButton.tag = FALSE
                }
                Log.i(TAG, "tag : ${likeButton.tag}")
                try{
                    RecipeService(activity, context, ServerAddress.serverAddress).
                        postLike(user.id!!, recipeDetailItem.id!!, flag, likeAmountTextView)
                }catch (e: NullPointerException){
                    Log.i(TAG, "recipe or user id is missed")
                }
            }
            // like 설정 --

//            // --list 설정
//            var hasInListFlag = false
//            for(userItem in recipeDetailItem.list){
//                if(userItem.id == user.id)
//                    hasInListFlag = true
//            }
            if(recipeDetailItem.type){
                var listFlag = false
                for(lt in recipeDetailItem.list){
                    if(lt.userId == user.id)
                        listFlag  = true
                }
                if(listFlag){
                    addListButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_playlist_add_pushed_24dp))
                    addListButton.tag = TRUE
                }
                addListButton.setOnClickListener {
                    pushAddListButton(user.id!!, recipeDetailItem.id!!)
                }
            }else{
                addListButton.visibility = GONE
            }
//            addListButton.setOnClickListener {  }
            // list 설정 --

            // -- comment 작업
            commentTextView.text = recipeDetailItem.commentCount.toString()
            commentButton.setOnClickListener {
                bundle.putInt("approachRoot", COMMENT)
                bottomNavigationView.visibility= GONE
                fragment.findNavController().navigate(R.id.action_home_to_recipeDetail, bundle)
            }
            // -- comment작업

        }

        fun pushAddListButton(userId:Int, recipeId:Int){
            if(addListButton.tag == TRUE){
                addListButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_playlist_add_black_24dp))
                addListButton.tag = FALSE
            }else{
                addListButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_playlist_add_pushed_24dp))
                addListButton.tag = TRUE
            }
            val service = RecipeService(activity, context, ServerAddress.serverAddress).service
            val call = service.postWishList(userId, recipeId)
            call.enqueue(object :  Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val responseBody = response.body()
                    Log.e(TAG, "$responseBody")
                    if(responseBody!!){
                        addListButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_playlist_add_pushed_24dp))
                        addListButton.tag = TRUE
                        Snackbar.make(layoutPage, "리스트에 추가되었습니다.", Snackbar.LENGTH_SHORT).show()
                    }else{
                        addListButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_playlist_add_black_24dp))
                        addListButton.tag = FALSE
                        Snackbar.make(layoutPage, "리스트에서 삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    call.cancel()
                }

            })
        }
    }

}