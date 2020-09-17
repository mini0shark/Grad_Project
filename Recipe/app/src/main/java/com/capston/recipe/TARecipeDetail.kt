package com.capston.recipe


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capston.recipe.Adapter.T3ImageViewPagerAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.*
import com.capston.recipe.Utils.ConstCodes
import com.capston.recipe.Utils.ConstCodes.COMMENT
import com.capston.recipe.Utils.ConstCodes.EDIT
import com.capston.recipe.Utils.ConstCodes.FALSE
import com.capston.recipe.Utils.ConstCodes.RECIPE
import com.capston.recipe.Utils.ConstCodes.TRUE
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_ta_recipe_detail.view.*
import kotlinx.android.synthetic.main.layout_ta_comment_for_show.view.*
import kotlinx.android.synthetic.main.include_comment_form.view.*
import kotlinx.android.synthetic.main.layout_ta_recipe_detail_container_item.view.*
import kotlinx.android.synthetic.main.layout_ta_recipe_detail_extratip.view.*
import kotlinx.android.synthetic.main.layout_ta_recipe_detail_ingredient_item.view.*
import kotlinx.android.synthetic.main.layout_ta_recipe_detail_reference.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TARecipeDetail : Fragment() {
    val TAG = "RecipeDetail"
    val TOP = 0
    val BOTTOM = 1
    lateinit var fragmentInflater:View
    var menu:Menu?=null
    var commentCall:Call<Boolean>? = null

    lateinit var recipeService: RecipeService
    var recipeCall:Call<RecipeDetailApiItem>?=null
    lateinit var recipeDetailItem:RecipeDetailApiItem

    private var storyCall:Call<StoryDetailApiItem>?=null
    lateinit var storyDetailItem:StoryDetailApiItem
    lateinit var sft:SharedPreferenceTool
    var typeOf = -1
    var recipeId:Int = -1
    var user:UserApiItem? = null
    lateinit var commentLayout:LinearLayout
    lateinit var commentList:ArrayList<CommentForRecipeDetailApiItem>
    var commentVolume = 0
    lateinit var inflater: LayoutInflater
    var container: ViewGroup? = null
    var opponentUserId = -1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentInflater = inflater.inflate(R.layout.fragment_ta_recipe_detail, container, false)
        recipeService  = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        commentLayout = fragmentInflater.layout_recipeDetail_comment
        sft = SharedPreferenceTool(context!!)
        val tempUser = sft.loadObjectSharedPreference<UserApiItem>(USER)
        if(tempUser!=null){
            user = tempUser
        }
        setHasOptionsMenu(true)
        this.inflater = inflater
        this.container = container


        return fragmentInflater
    }
    fun callDetail(inflater: LayoutInflater, container: ViewGroup?){
        recipeId = arguments?.getInt("recipeId")!!
        typeOf = arguments?.getInt("type")!!
        Log.i(TAG, "type  : $typeOf")
        Log.i(TAG, "recipeId  : $recipeId")
        val service = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        if(typeOf== RECIPE) {
            recipeCall = service.service.getRecipeItem(recipeId)
            recipeCall!!.enqueue(object : Callback<RecipeDetailApiItem> {
                override fun onResponse(
                    call: Call<RecipeDetailApiItem>,
                    response: Response<RecipeDetailApiItem>
                ) {
                    val responseItem = response.body()
                    if (responseItem != null) {
                        recipeDetailItem = responseItem
                        handleRecipeItem(inflater, container)
                        if(recipeDetailItem.user?.id ==user?.id ) {
                            activity!!.menuInflater.inflate(R.menu.toolbar_menu_recipe_detail_recipe_writer, menu)
                        }
                        else{
                            activity!!.menuInflater.inflate(R.menu.toolbar_menu_recipe_detail_for_normal, menu)
                        }
                        opponentUserId =recipeDetailItem.user!!.id
                    } else {
                        Toast.makeText(context!!, "아이템을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
//                        fragmentManager!!.beginTransaction().remove(this@TARecipeDetail).commit()
                        findNavController().popBackStack()
                    }

                }

                override fun onFailure(call: Call<RecipeDetailApiItem>, t: Throwable) {
                    Log.e(TAG, "fail to retrofit")
                    call.cancel()
                    t.printStackTrace()
//                    fragmentManager!!.beginTransaction().remove(this@TARecipeDetail).commit()
                    findNavController().popBackStack()
                }

            })
        }else{
            (activity as AppCompatActivity).supportActionBar?.title = "STORY"
            storyCall = service.service.getStoryItem(recipeId)
            storyCall!!.enqueue(object : Callback<StoryDetailApiItem> {
                override fun onResponse(
                    call: Call<StoryDetailApiItem>,
                    response: Response<StoryDetailApiItem>
                ) {
                    val responseItem = response.body()
                    if (responseItem != null) {
                        storyDetailItem = responseItem
                        handleStoryItem(inflater, container)

                        if(storyDetailItem.user?.id==user?.id ) {
                            activity!!.menuInflater.inflate(R.menu.toolbar_menu_recipe_detail_story_writer, menu)
                        }
                        opponentUserId =storyDetailItem.user!!.id
                    } else {
                        Toast.makeText(context!!, "아이템을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
//                        fragmentManager!!.beginTransaction().remove(this@TARecipeDetail).commit()
                        findNavController().popBackStack()
                    }

                }

                override fun onFailure(call: Call<StoryDetailApiItem>, t: Throwable) {
                    Log.e(TAG, "fail to retrofit")
                    call.cancel()
                    t.printStackTrace()
//                    fragmentManager!!.beginTransaction().remove(this@TARecipeDetail).commit()
                    findNavController().popBackStack()
                }

            })
        }
    }

    fun moveSpecificView(view:View){
        val scrollView = fragmentInflater.scrolView_recipeDetail
        scrollView.scrollTo(0, view.top)
    }
    fun handleRecipeItem(inflater: LayoutInflater, container: ViewGroup?){
        // 배경 터치시 키보드 내리기
        fragmentInflater.linearLayout_recipeDetail_visible.setOnClickListener {
            hideKeyboard()
        }
        fragmentInflater.linearLayout_recipeDetail_visible.visibility=View.VISIBLE
        fragmentInflater.layout_recipeDetail_loading.visibility=View.GONE
        // 타이틀
        (activity as AppCompatActivity).supportActionBar?.title = recipeDetailItem.recipeTitle
//        fragmentInflater.layout_recipeDetail_recipeOnly1.visibility = View.VISIBLE
        fragmentInflater.layout_recipeDetail_recipeOnly2.visibility = View.VISIBLE
        fragmentInflater.layout_recipeDetail_recipeOnly3.visibility = View.VISIBLE
        fragmentInflater.layout_recipeDetail_recipeOnly4.visibility = View.VISIBLE




        // --사용자 프로필세팅
        if(recipeDetailItem.user != null) {
            ImageUtils.putImageIntoCircleView(context!!, recipeDetailItem.user!!.profileImage, fragmentInflater.imageView_recipeDetail_userImage)
            fragmentInflater.textView_recipeDetail_userName.text = recipeDetailItem.user!!.nickname
            fragmentInflater.layout_recipeDetail_user.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("userId", recipeDetailItem.user!!.id)
                findNavController().navigate(R.id.action_recipeDetail_to_otherUserPage, bundle)
            }
        }else{
            fragmentInflater.textView_recipeDetail_userName.text = "탈퇴한 사용자 입니다."
            fragmentInflater.imageView_recipeDetail_userImage.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.basic_profile_image))
        }
        // 사용자 프로필세팅--


        // -- hitCount
        fragmentInflater.textView_recipeDetail_hitCount.text = recipeDetailItem.hitCount.toString()
        // hitCount--


        //--메인 이미지 세팅
        val imageList= arrayListOf<String?>()
        if(recipeDetailItem.multiImageResult!=null) {
            for (contentImage in recipeDetailItem.multiImageResult!!) {
                imageList.add(contentImage.image)
            }
        }else{
            imageList.add(null)
        }
        Log.i(TAG, "multiImageSize : ${imageList.size}")
        fragmentInflater.viewPager_recipeDetail.clipToPadding = false
        fragmentInflater.viewPager_recipeDetail.adapter = T3ImageViewPagerAdapter(context!!, imageList)
        //이미지 세팅--
        // --레시피 Name 설정 & 세부내용
        fragmentInflater.textView_recipeDetail_recipeName.text = recipeDetailItem.foodName
        if(recipeDetailItem.introduceRecipe!=null) {
            fragmentInflater.textView_recipeDetail_introduce.text = recipeDetailItem.introduceRecipe
        }
        // 레시피 title 설정 & 세부내용--


        // --좋아요, 내 목록 추가,
        if(user!=null) {
            val likeButton =fragmentInflater.button_recipeDetail_like
            val likeVolumeText = fragmentInflater.textView_recipeDetail_likeAmount
            var likeFlag = false
            val userId= user!!.id!!
            for (userItem in recipeDetailItem.likes) {
                if (userItem.userId == userId)
                    likeFlag = true
            }
            if (likeFlag) {
                likeButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_thumb_up_pushed_24dp))
                likeButton.tag = TRUE
            }
            likeVolumeText.text = recipeDetailItem.likes.size.toString()
            likeButton.setOnClickListener {
                var flag = likeButton.tag == FALSE
                if(flag){
                    likeButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_thumb_up_pushed_24dp))
                    likeButton.tag = TRUE
                }else{
                    likeButton.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_thumb_up_black_24dp))
                    likeButton.tag = FALSE
                }
                Log.i(TAG, "tag : ${likeButton.tag}")
                try{
                    recipeService.
                        postLike(userId, recipeDetailItem.id!!, flag, likeVolumeText)
                }catch (e: NullPointerException){
                    Log.i(TAG, "recipe or user id is missed")
                }
            }

            if(recipeDetailItem.type){
                var listFlag = false
                for(lt in recipeDetailItem.list){
                    if(lt.userId == user!!.id)
                        listFlag  = true
                }
                if(listFlag){
                    fragmentInflater.button_recipeDetail_addToMyList
                        .setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_playlist_add_pushed_24dp))
                    fragmentInflater.button_recipeDetail_addToMyList.tag = TRUE
                }
                fragmentInflater.button_recipeDetail_addToMyList.setOnClickListener {
                    pushAddListButton(user!!.id!!, recipeDetailItem.id!!)
                }
            }else{
                fragmentInflater.button_recipeDetail_addToMyList.visibility = View.GONE
            }

//          리스트 !! 나중에 꼭 다시하기 !!!!!!!!!!!!!!!!!!!1
//            var hasInListFlag = false
//            for(userItem in recipeDetailItem.list){
//                if(userItem.userId == userId)
//                    hasInListFlag = true
//            }
//            if(hasInListFlag){
//                fragmentInflater.button_recipeDetail_addToMyList.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_thumb_up_pushed_24dp))
//            }
        }
        // 좋아요, 내 목록 추가 --


        // -- comment 클릭시 맨 아래로 내리기( 맨 위쪽
        fragmentInflater.button_recipeDetail_comment.setOnClickListener {
            moveSpecificView(fragmentInflater.layout_recipeDetail_commentWrapper)
        }
        fragmentInflater.textView_recipeDetail_commentAmount.text = recipeDetailItem.comment.size.toString()
        // -- comment작업

        // --category, 기준인원, 소요시간
        fragmentInflater.textView_recipeDetail_category.text = recipeDetailItem.category
        fragmentInflater.textView_recipeDetail_forPerson.text = recipeDetailItem.forPerson.toString()
        val requiredTime = recipeDetailItem.requiredTime!!
        fragmentInflater.textView_recipeDetail_requiredTime.text = requiredTime.toString()
        fragmentInflater.textView_recipeDetail_requiredTime_toHour.text = String.format("%.1f",(requiredTime.toFloat()/60))
        // category, 기준인원, 소요시간--

        // -- 재료
        if(recipeDetailItem.ingredients!=null) {
            for (ingredient in recipeDetailItem.ingredients!!){
                val ingredientUnitLayout = inflater.inflate(R.layout.layout_ta_recipe_detail_ingredient_item, container, false)
                ingredientUnitLayout.textView_layoutRecipeDetailIngredient_ingredient.text = ingredient.ingredient
                ingredientUnitLayout.textView_layoutRecipeDetailIngredient_amount.text = ingredient.amount.toString()
                fragmentInflater.linearLayout_recipeDetail_ingredients.addView(ingredientUnitLayout)
            }
        }
        // 재료 --

        // -- Recipe 나열
        if(recipeDetailItem.recipeOrder!=null) {
            for (recipe in recipeDetailItem.recipeOrder!!){
                val recipeUnitLayout = inflater.inflate(R.layout.layout_ta_recipe_detail_container_item, container, false)
                // image
                ImageUtils.putImageIntoView(context!!, recipe.image, recipeUnitLayout.imageView_layoutRecipeContainer_recipe)
                recipeUnitLayout.textView_layoutRecipeContainer_number.text = recipe.order.toString()
                recipeUnitLayout.textView_layoutRecipeContainer_recipe.text = recipe.explain
                Log.i(TAG, "$recipe")
                fragmentInflater.linearLayout_recipeDetail_recipe.addView(recipeUnitLayout)

            }
        }
        // Recipe 나열 --

        // --Extra tips 나열
        if(recipeDetailItem.extraTip!=null) {
            for (tip in recipeDetailItem.extraTip!!){
                val tipUnitLayout = inflater.inflate(R.layout.layout_ta_recipe_detail_extratip, container, false)
                Log.i(TAG, tip.toString())
                tipUnitLayout .textView_layoutRecipeDetailExtraTip_tip.text = tip.tip
                fragmentInflater.linearLayout_recipeDetail_extraTips.addView(tipUnitLayout )
            }
        }
        // Extra tips 나열--
        // -- comment작업
        val commentEditText = fragmentInflater.include_submitComment.editText_layoutCommentForm_comment
        fragmentInflater.include_submitComment.button_layoutCommentForm_submit.setOnClickListener {
            if(commentEditText!=null || commentEditText!!.text.toString() != ""){
                val comment = CommentApiItem(user = user!!.id!!, recipeContainer = recipeDetailItem.id!!, text=commentEditText.text.toString())
                val call = recipeService.service.postRegisterRecipeComment(comment)
                call.enqueue(object : Callback<CommentForRecipeDetailApiItem>{
                    override fun onResponse(call: Call<CommentForRecipeDetailApiItem>, response: Response<CommentForRecipeDetailApiItem>) {
                        val responseBody = response.body()
                        val comAmt = fragmentInflater.textView_recipeDetail_commentAmount
                        comAmt.text = "${comAmt.text.toString().toInt()+1}"
                        if(responseBody!=null) {
                            addToComment(responseBody, inflater, container, TOP)
                            commentEditText.text.clear()
                            hideKeyboard()
                        }
                        Log.i(TAG, "responseBody : $responseBody")
                    }

                    override fun onFailure(call: Call<CommentForRecipeDetailApiItem>, t: Throwable) {
                        Log.i(TAG, "responseBody : fail")
                        t.printStackTrace()
                    }

                })
            }
        }
        commentEditText.setOnFocusChangeListener { _, hasFocus ->
            //            bottomNavigationControll(hasFocus)
        }
        val approachRoot = arguments!!.getInt("approachRoot")
        if(approachRoot == COMMENT){
            fragmentInflater.layout_recipeDetail_commentWrapper.isFocusableInTouchMode = true
            fragmentInflater.layout_recipeDetail_commentWrapper.requestFocus()
        }else{
            fragmentInflater.layout_recipeDetail_user.isFocusableInTouchMode = true
            fragmentInflater.layout_recipeDetail_user.requestFocus()
        }
        commentList = recipeDetailItem.comment
        addCommentList(inflater, container, true)
        fragmentInflater.button_recipeDetail_moreComment.setOnClickListener {
            addCommentList(inflater, container, false)
        }

        // comment 작업 --

    }
    fun handleStoryItem(inflater: LayoutInflater, container: ViewGroup?){
        // 배경 터치시 키보드 내리기
        fragmentInflater.button_recipeDetail_addToMyList.visibility = View.GONE
        fragmentInflater.linearLayout_recipeDetail_visible.setOnClickListener {
            hideKeyboard()
        }
        fragmentInflater.linearLayout_recipeDetail_visible.visibility=View.VISIBLE
        fragmentInflater.layout_recipeDetail_loading.visibility=View.GONE
        // 타이틀

        //레시피용 레이아웃 지우기
//        fragmentInflater.layout_recipeDetail_recipeOnly1.visibility = View.GONE
        fragmentInflater.layout_recipeDetail_recipeOnly2.visibility = View.GONE
        fragmentInflater.layout_recipeDetail_recipeOnly3.visibility = View.GONE
        fragmentInflater.layout_recipeDetail_recipeOnly4.visibility = View.GONE



        // --사용자 프로필세팅
        if(storyDetailItem.user != null) {
            ImageUtils.putImageIntoCircleView(context!!, storyDetailItem.user!!.profileImage, fragmentInflater.imageView_recipeDetail_userImage)
            fragmentInflater.textView_recipeDetail_userName.text = storyDetailItem.user!!.nickname
            val bundle = Bundle()
            fragmentInflater.layout_recipeDetail_user.setOnClickListener {
                bundle.putInt("userId", storyDetailItem.user!!.id)
                findNavController().navigate(R.id.action_recipeDetail_to_otherUserPage, bundle)
            }
        }else{
            fragmentInflater.textView_recipeDetail_userName.text = "탈퇴한 사용자 입니다."
            fragmentInflater.imageView_recipeDetail_userImage.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.basic_profile_image))
        }
        // 사용자 프로필세팅--


        // -- hitCount
        fragmentInflater.textView_recipeDetail_hitCount.text = storyDetailItem.hitCount.toString()
        // hitCount--


        //--메인 이미지 세팅
        val imageList= arrayListOf<String?>()
        if(storyDetailItem.multiImageResult!=null) {
            for (contentImage in storyDetailItem.multiImageResult!!) {
                imageList.add(contentImage.image)
            }
        }else{
            imageList.add(null)
        }
        Log.i(TAG, "multiImageSize : ${imageList.size}")
        fragmentInflater.viewPager_recipeDetail.clipToPadding = false
        fragmentInflater.viewPager_recipeDetail.adapter = T3ImageViewPagerAdapter(context!!, imageList)
        //이미지 세팅--
        // -- 내용 세팅
        if(storyDetailItem.introduceRecipe!=null) {
            fragmentInflater.textView_recipeDetail_introduce.text = storyDetailItem.introduceRecipe
        }
        // 내용 세팅--



        // --참조나열 나열
        if(storyDetailItem.references.size>0) {
            fragmentInflater.textView_recipeDetail_reference.visibility = View.VISIBLE
            fragmentInflater.linearLayout_recipeDetail_reference.visibility = View.VISIBLE
        }
        for (ref in storyDetailItem.references){
            val referenceUnitLayout = inflater.inflate(R.layout.layout_ta_recipe_detail_reference, container, false)
            Log.i(TAG, "ref : $ref")
            if(ref.recipeOriginal.user !=null ) {
                referenceUnitLayout.textView_layoutRecipeDetailReference_userNickname.text =
                    ref.recipeOriginal.user!!.nickname
            }
            else {
                referenceUnitLayout.textView_layoutRecipeDetailReference_userNickname.text =
                    "탈퇴한 사용자"
            }
            ImageUtils.putImageIntoView(context!!, ref.recipeOriginal.user!!.profileImage
                , referenceUnitLayout.imageView_layoutRecipeDetailReference_userImage)
            referenceUnitLayout.textView_layoutRecipeDetailReference_RecipeTitle.text = ref.recipeOriginal.recipeTitle
            referenceUnitLayout.setOnClickListener {
                // 해당 레시피 보기
                var bundle= Bundle()
                bundle.putInt("recipeId", ref.recipeOriginal.id)
                bundle.putInt("type", RECIPE)
                findNavController().navigate(R.id.action_recipeDetail_self, bundle)
            }
            fragmentInflater.linearLayout_recipeDetail_reference.addView(referenceUnitLayout )
        }
        // 참조 나열 --




        // --좋아요
        if(user!=null) {
            val likeButton = fragmentInflater.button_recipeDetail_like
            val likeVolumeText = fragmentInflater.textView_recipeDetail_likeAmount
            var likeFlag = false
            val userId = user!!.id!!
            for (userItem in storyDetailItem.likes) {
                if (userItem.userId == userId)
                    likeFlag = true
            }
            if (likeFlag) {
                likeButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_thumb_up_pushed_24dp
                    )
                )
                likeButton.tag = TRUE
            }
            likeVolumeText.text = storyDetailItem.likes.size.toString()
            likeButton.setOnClickListener {
                var flag = likeButton.tag == FALSE
                if (flag) {
                    likeButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_thumb_up_pushed_24dp
                        )
                    )
                    likeButton.tag = TRUE
                } else {
                    likeButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_thumb_up_black_24dp
                        )
                    )
                    likeButton.tag = FALSE
                }
                Log.i(TAG, "tag : ${likeButton.tag}")
                try {
                    recipeService.postLike(
                        userId,
                        storyDetailItem.id!!,
                        flag,
                        likeVolumeText
                    )
                } catch (e: NullPointerException) {
                    Log.i(TAG, "recipe or user id is missed")
                }
            }
        }
        //  내 목록 추가 --


        // -- comment 클릭시 맨 아래로 내리기( 맨 위쪽
        fragmentInflater.button_recipeDetail_comment.setOnClickListener {
            moveSpecificView(fragmentInflater.layout_recipeDetail_commentWrapper)
        }
        fragmentInflater.textView_recipeDetail_commentAmount.text = storyDetailItem.comment.size.toString()
        // -- comment작업


        // -- comment작업
        val commentEditText = fragmentInflater.include_submitComment.editText_layoutCommentForm_comment
        fragmentInflater.include_submitComment.button_layoutCommentForm_submit.setOnClickListener {
            if(commentEditText!=null || commentEditText!!.text.toString() != ""){
                val comment = CommentApiItem(user = user!!.id!!, recipeContainer = storyDetailItem.id!!, text=commentEditText.text.toString())
                val call = recipeService.service.postRegisterRecipeComment(comment)
                call.enqueue(object : Callback<CommentForRecipeDetailApiItem>{
                    override fun onResponse(call: Call<CommentForRecipeDetailApiItem>, response: Response<CommentForRecipeDetailApiItem>) {
                        val responseBody = response.body()
                        if(responseBody!=null) {
                            addToComment(responseBody, inflater, container, TOP)
                            commentEditText.text.clear()
                            hideKeyboard()
                        }
                        Log.i(TAG, "responseBody : $responseBody")
                    }

                    override fun onFailure(call: Call<CommentForRecipeDetailApiItem>, t: Throwable) {
                        Log.i(TAG, "responseBody : fail")
                        t.printStackTrace()
                    }

                })
            }
        }
        commentEditText.setOnFocusChangeListener { _, hasFocus ->
            //            bottomNavigationControll(hasFocus)
        }
        val approachRoot = arguments!!.getInt("approachRoot")
        if(approachRoot == COMMENT){
            fragmentInflater.layout_recipeDetail_commentWrapper.isFocusableInTouchMode = true
            fragmentInflater.layout_recipeDetail_commentWrapper.requestFocus()
        }else{
            fragmentInflater.layout_recipeDetail_user.isFocusableInTouchMode = true
            fragmentInflater.layout_recipeDetail_user.requestFocus()
        }
        commentList = storyDetailItem.comment
        addCommentList(inflater, container, true)
        fragmentInflater.button_recipeDetail_moreComment.setOnClickListener {
            addCommentList(inflater, container, false)
        }

        // comment 작업 --

    }
    private fun hideKeyboard(){
        val inputMethodManager= activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
    fun addToComment(comment:CommentForRecipeDetailApiItem, inflater: LayoutInflater, container: ViewGroup?, addTo:Int) {
        val newComment = inflater.inflate(R.layout.layout_ta_comment_for_show, container, false)
        ImageUtils.putImageIntoCircleView(
            context!!,
            comment.user.profileImage,
            newComment.imageView_comment_userImage
        )
        newComment.textView_comment_userName.text = comment.user.nickname
        newComment.textView_comment_content.text = comment.text
        newComment.button_comment_like.tag = "False"
        newComment.imageView_comment_userImage.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("userId", comment.user.id)
            findNavController().navigate(R.id.action_recipeDetail_to_otherUserPage, bundle)
        }
        if(typeOf == RECIPE){
            if (comment.user.id != user!!.id && recipeDetailItem.user!!.id != user!!.id) {
                newComment.button_comment_delete.visibility = View.GONE
            } else {
                newComment.button_comment_delete.setOnClickListener {
                    val call = recipeService.service.deleteComment(comment.id!!)
                    call.enqueue(object:Callback<ResponseBody>{
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            call.cancel()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            val responseBody = response.body()
                            if(responseBody!=null){
                                newComment.visibility = View.GONE
                                Toast.makeText(context!!, "삭제되었습니다..", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(context!!, "통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                    })

                }

            }
        }else{

            if (comment.user.id != user!!.id && storyDetailItem.user!!.id != user!!.id) {
                newComment.button_comment_delete.visibility = View.GONE
            } else {
                newComment.button_comment_delete.setOnClickListener {
                    val call = recipeService.service.deleteComment(comment.id!!)
                    call.enqueue(object:Callback<ResponseBody>{
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            call.cancel()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            val responseBody = response.body()
                            if(responseBody!=null){
                                newComment.visibility = View.GONE
                                Toast.makeText(context!!, "삭제되었습니다..", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(context!!, "통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                    })

                }

            }
        }
        if(comment.userLike != null)
            for(like in comment.userLike!!){
                if(user!!.id == like) {
                    newComment.button_comment_like.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_thumb_up_pushed_24dp
                        )
                    )
                    newComment.button_comment_like.tag = "True"
                    break
                }
            }
        newComment.button_comment_like.setOnClickListener {
            val flag = it.tag == "True"
            commentCall = recipeService.service
                .postClickCommentLike(user!!.id!!, comment.id!!)
            commentCall!!.enqueue(object : Callback<Boolean>{
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    call.cancel()
                    Toast.makeText(context!!, "통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val responseBody = response.body()
                    if( responseBody!= null)
                        if(responseBody!!){
                            newComment.button_comment_like.tag = "True"
                            newComment.button_comment_like.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context!!,
                                    R.drawable.ic_thumb_up_pushed_24dp
                                )
                            )
                            newComment.textView_comment_likeAmount.text = (newComment.textView_comment_likeAmount.text.toString().toInt()+1).toString()
                        }else{
                            newComment.button_comment_like.tag = "False"
                            newComment.button_comment_like.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context!!,
                                    R.drawable.ic_thumb_up_black_24dp
                                )
                            )
                            if(newComment.textView_comment_likeAmount.text.toString().toInt()>0)
                                newComment.textView_comment_likeAmount.text = (newComment.textView_comment_likeAmount.text.toString().toInt()-1).toString()
                        }
                }

            })
        }

        val commentLike = comment.userLike  //user_id목록
        if(commentLike!=null){
            newComment.textView_comment_likeAmount.text = commentLike.size.toString()
        }
        var toWhere = addTo
        if(toWhere == BOTTOM)
            toWhere = commentLayout.size

        commentLayout.addView(newComment, toWhere)
    }
    private fun addCommentList(inflater: LayoutInflater, container: ViewGroup?,init:Boolean){
        var add = 5
        if(commentList.size != commentVolume) {
            if (commentList.size < commentVolume + 5)
                add = commentList.size - commentVolume
            for (comLoc in commentVolume until commentVolume + add) {
                addToComment(commentList[comLoc], inflater, container, BOTTOM)
            }
            commentVolume += add
        }else{
            if(!init)
                Toast.makeText(context, "더이상 댓글이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(recipeCall!=null )
            if(!recipeCall!!.isCanceled)
                recipeCall!!.cancel()
        if(storyCall!=null )
            if(!storyCall!!.isCanceled)
                storyCall!!.cancel()
        if(commentCall!=null)
            if(!commentCall!!.isCanceled)
                commentCall!!.cancel()
    }

    override fun onResume() {
        super.onResume()

        while(findNavController().currentDestination!!.id!=R.id.recipeDetail)
            findNavController().popBackStack()
        callDetail(inflater, container)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        Log.i("tag", "oncreate menu")
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun deleteRecipe(){
        var call = recipeService.service.deleteRecipe(recipeId)
        call.enqueue(object: Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code()<300) {
                    Toast.makeText(context!!, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }else{
                    Toast.makeText(context!!, "삭제하지 못했습니다 차후에 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context!!, "통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun editRecipe(){
        val bundle = Bundle()
        bundle.putInt("approachRoot", EDIT)
        bundle.putInt("recipeId", recipeId)
        if(typeOf== RECIPE)
            findNavController().navigate(R.id.action_recipeDetail_to_registerRecipe  , bundle)
        else
            findNavController().navigate(R.id.action_recipeDetail_to_registerFoodStory  , bundle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuItem_recipeDetail_remove->{
                val builder = AlertDialog.Builder(context!!)
                var type = if(typeOf== RECIPE) "레시피"  else "스토리"
                builder.setTitle("알림").setMessage("${type}을 지우겠습니까?")
                builder.setPositiveButton("OK") { _, _ ->
                    deleteRecipe()
                }
                builder.setNegativeButton("CANCEL") { _, _ ->
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
            R.id.menuItem_recipeDetail_edit->{
                val builder = AlertDialog.Builder(context!!)
                builder.setTitle("알림").setMessage("수정하시겠습니까?")
                builder.setPositiveButton("OK") { _, _ ->
                    editRecipe()
                }
                builder.setNegativeButton("CANCEL") { _, _ ->

                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
            R.id.menuItem_recipeDetail_follow->{
                val bundle = Bundle()
                bundle.putInt("approachRoot", ConstCodes.RECIPE_DETAIL)
                bundle.putInt("recipeId", recipeId)
                bundle.putString("title", recipeDetailItem.recipeTitle)
                findNavController().navigate(R.id.action_recipeDetail_to_registerFoodStory  , bundle)
            }
            R.id.menuItem_recipeDetail_question->{
                val bundle = Bundle()
                bundle.putInt("recipeId", recipeId)
                bundle.putInt("opponentUserId", opponentUserId)
                findNavController().navigate(R.id.action_recipeDetail_to_chatting  , bundle)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun pushAddListButton(userId:Int, recipeId:Int){
        if(fragmentInflater.button_recipeDetail_addToMyList.tag == TRUE){
            fragmentInflater.button_recipeDetail_addToMyList
                .setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_playlist_add_black_24dp))
            fragmentInflater.button_recipeDetail_addToMyList.tag = FALSE
        }else{
            fragmentInflater.button_recipeDetail_addToMyList.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_playlist_add_pushed_24dp))
            fragmentInflater.button_recipeDetail_addToMyList.tag = TRUE
        }
        val call = recipeService.service.postWishList(userId, recipeId)
        call.enqueue(object :  Callback<Boolean>{
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val responseBody = response.body()
                Log.e(TAG, "$responseBody")
                if(responseBody!!){
                    fragmentInflater.button_recipeDetail_addToMyList
                        .setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_playlist_add_pushed_24dp))
                    fragmentInflater.button_recipeDetail_addToMyList.tag = TRUE
                    Snackbar.make(fragmentInflater, "리스트에 추가되었습니다.", Snackbar.LENGTH_SHORT).show()
                }else{
                    fragmentInflater.button_recipeDetail_addToMyList
                        .setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_playlist_add_black_24dp))
                    fragmentInflater.button_recipeDetail_addToMyList.tag = FALSE
                    Snackbar.make(fragmentInflater, "리스트에서 삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                call.cancel()
            }

        })
    }

}

