package com.capston.recipe

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capston.recipe.Adapter.T3ImageViewPagerRegisterAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.*
import com.capston.recipe.Utils.*
import com.capston.recipe.Utils.ConstCodes.EDIT
import com.capston.recipe.Utils.ConstCodes.TAG_EVENT_DIALOG
import kotlinx.android.synthetic.main.fragment_t3_register_food_story.view.*
import kotlinx.android.synthetic.main.layout_t3_reference_recipe.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class T3RegisterFoodStory : Fragment() {
    val TAG = "RegisterFoodStory tag"
    lateinit var fragmentInflater:View
    private val imageList = arrayListOf<Bitmap>()
    val imagePathList= arrayListOf<String?>()
    //    private lateinit var mAdapter:AddImageRecyclerViewAdapterForStory
    lateinit var mAdapter:T3ImageViewPagerRegisterAdapter
    lateinit var targetView:View
    lateinit var blankCall: Call<StoryDetailApiItem>

    private val references = arrayListOf<View>()
    private val referenceIdList= arrayListOf<Int>()
    var recipeId = -1

    lateinit var user:UserApiItem
    lateinit var sft:SharedPreferenceTool
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sft = SharedPreferenceTool(context!!)
        try {
            user = GettingUser.getUserInfo(context!!)!!
        }catch (e:NullPointerException){
            e.printStackTrace()
            GettingUser.sft.saveObjectSharedPreference(USER, null)
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
            return null
        }
        mAdapter = T3ImageViewPagerRegisterAdapter(context!!, imageList, this, imagePathList)
        fragmentInflater = inflater.inflate(R.layout.fragment_t3_register_food_story, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Registration - Story"
        fragmentInflater.layout_registerFoodStory_mainLayout.setOnClickListener {
            hideKeyboard()
        }
        val viewPager = fragmentInflater.viewPager_addFoodImage
        viewPager.clipToPadding = false
        viewPager.adapter = mAdapter

        fragmentInflater.button_addImages.setOnClickListener {
            //=> EditMultpleImage로
            if(imageList!!.size>0){
                alertHandler()
                // fix - alert dialog\ 해서 기존이미지를 다 지우고 다시 등록할 건지 물어보기
            }else{
                multipleImageFragmentStarter()
            }
        }
        val approachRoot = arguments?.getInt("approachRoot")
        if(approachRoot == ConstCodes.RECIPE_DETAIL){
            val linearLayout = inflater.inflate(R.layout.layout_t3_reference_recipe,container,false)
            fragmentInflater.findViewById<LinearLayout>(R.id.linearLayout_referenceRecipe).addView(linearLayout)
            linearLayout.button_removeReference.setOnClickListener {
                Toast.makeText(context!!, "이 레시피는 제거할 수 없습니다..", Toast.LENGTH_SHORT).show()
            }
            linearLayout.button_findReference.setOnClickListener{
                Toast.makeText(context!!, "이 레시피는 수정 할 수 없습니다..", Toast.LENGTH_SHORT).show()
            }
            val title =  arguments?.getString("title")
            val recipeId =  arguments?.getInt("recipeId")
            linearLayout .textView_referenceRecipe.text = title
            linearLayout .textView_referenceRecipe.tag = recipeId.toString()
            linearLayout .layout_referenceRecipe.setBackgroundColor(ContextCompat.getColor(context!!,R.color.colorPrimary))
            references.add(linearLayout)
        }

        if(approachRoot== ConstCodes.EDIT){
            (activity as AppCompatActivity).supportActionBar?.title = "수정 - Story"
            settingBlank(inflater, container)
        }
        else{
            (activity as AppCompatActivity).supportActionBar?.title = "등록 - Story"
        }
        fragmentInflater.button_addReference.setOnClickListener {
            if(references.size<3){
                val linearLayout = inflater.inflate(R.layout.layout_t3_reference_recipe,container,false)
                fragmentInflater.findViewById<LinearLayout>(R.id.linearLayout_referenceRecipe).addView(linearLayout)
                linearLayout.button_removeReference.setOnClickListener {
                    fragmentInflater.findViewById<LinearLayout>(R.id.linearLayout_referenceRecipe).removeView(linearLayout)
                    references.remove(linearLayout)
                }
                linearLayout.button_findReference.setOnClickListener{
                    targetView=linearLayout
                    showSearchReferenceDialog()
                }
                references.add(linearLayout)
            }else{
                Toast.makeText(context!!, "레시피는 3개 까지 참조 할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        fragmentInflater.button_registerFoodStory.setOnClickListener {
            registerFoodStory()
        }
        return fragmentInflater
    }

    private fun settingBlank(inflater: LayoutInflater, container: ViewGroup?) {
        recipeId =arguments?.getInt("recipeId")!!
        fragmentInflater.button_registerFoodStory.text = "수정"


        val service = RecipeService(activity!!, context!!, ServerAddress.serverAddress).service
        blankCall = service.getStoryItem(recipeId!!)
        blankCall.enqueue(object :Callback<StoryDetailApiItem>{
            override fun onResponse(call: Call<StoryDetailApiItem>, response: Response<StoryDetailApiItem>) {
                val responseBody= response.body()
                if(responseBody != null){
                    fragmentInflater.editText_foodStoryContent.setText(responseBody.introduceRecipe)
                    for(ref in responseBody.references){
                        val linearLayout = inflater.inflate(R.layout.layout_t3_reference_recipe,container,false)
                        linearLayout.textView_referenceRecipe.text = ref.recipeOriginal.recipeTitle
                        linearLayout.textView_referenceRecipe.tag = ref.recipeOriginal.id.toString()
                        linearLayout.layout_referenceRecipe.setBackgroundColor(ContextCompat.getColor(context!!,R.color.colorPrimary))
                        fragmentInflater.findViewById<LinearLayout>(R.id.linearLayout_referenceRecipe).addView(linearLayout)
                        linearLayout.button_removeReference.setOnClickListener {
                            fragmentInflater.findViewById<LinearLayout>(R.id.linearLayout_referenceRecipe).removeView(linearLayout)
                            references.remove(linearLayout)
                        }
                        linearLayout.button_findReference.setOnClickListener{
                            targetView=linearLayout
                            showSearchReferenceDialog()
                        }
                        linearLayout
                        references.add(linearLayout)
                    }
                    //사진 처리
                    Log.i(TAG, responseBody.multiImageResult.toString())
                    if(responseBody.multiImageResult!=null) {
                        for (contentImage in responseBody.multiImageResult!!) {
                            imagePathList.add(contentImage.image)
                        }
                        mAdapter.notifyDataSetChanged()
                    }else{
                        imagePathList.add(null)
                    }
                }
            }

            override fun onFailure(call: Call<StoryDetailApiItem>, t: Throwable) {
                Toast.makeText(context!!, "통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                call.cancel()
                fragmentManager!!.popBackStack()
            }

        })

    }

    private fun alertHandler(){
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("알림").setMessage("선택한 사진을 초기화 하고 새로 선택하시겠습니까?")
        builder.setPositiveButton("OK") { _, _ ->
            multipleImageFragmentStarter()
        }
        builder.setNegativeButton("CANCEL") { _, _ ->

        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun multipleImageFragmentStarter(){
        fragmentInflater.frameLayout_registerFoodStory.visibility = View.VISIBLE
        val imageAddFragment = EditMultipleImageFragment()
        val transaction = fragmentManager!!.beginTransaction()
        transaction.add(R.id.frameLayout_registerFoodStory, imageAddFragment, "edit")
        transaction.addToBackStack(null)
        transaction.commit()
        fragmentManager!!.addOnBackStackChangedListener {
        }
        imageAddFragment.setDoneClickListener(object: EditMultipleImageFragment.DoneClickListener{
            override fun onClickDone(imgList: java.util.ArrayList<Bitmap>) {
                imageList.clear()
                imageList.addAll(imgList)
                mAdapter.notifyDataSetChanged()
            }

            override fun onCancel() {
                (activity!! as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
                fragmentInflater.frameLayout_registerFoodStory.visibility = View.GONE
                fragmentInflater.scrollView_registerFoodStory.visibility = View.VISIBLE
            }
        })
    }

    private fun registerFoodStory(){
        val storyImages = arrayListOf<Bitmap>()
        val content = fragmentInflater.editText_foodStoryContent.text.toString()
        val referenceRecipe = arrayListOf<Int>()    // 레시피 pk
        if(imageList!!.size>0 && content != "") {
            if(imagePathList.size==0)
                for (image in imageList!!){
                    storyImages.add(image)
                }
            if (references.size > 0)
                for (reference in references)
                    if (reference.textView_referenceRecipe.text.toString() != "")
                        referenceRecipe.add(reference.textView_referenceRecipe.tag.toString().toInt())
        }
        when{
            imagePathList.size==0 && imageList!!.size<1 ->{
                Toast.makeText(context!!, "한개이상의 사진이 필요합니다." , Toast.LENGTH_SHORT).show()
            }
            content == "" ->{
                Toast.makeText(context!!, "내용을 입력해 주세요" , Toast.LENGTH_SHORT).show()
            }
            else ->{
                for(ref in references){
                    try{
                        referenceIdList.add(ref.textView_referenceRecipe.tag.toString().toInt())
                    }catch (e:Exception){}
                }
                val approachRoot = arguments?.getInt("approachRoot")
                if(approachRoot== EDIT){
                    edit(content, storyImages)
                }else{
                    register(content, storyImages)
                }
                findNavController().popBackStack()
            }
        }
    }
    private fun edit(content:String, storyImages: ArrayList<Bitmap>){
        val storyItem = RecipeContainerApiItem(id=recipeId, introduceRecipe = content)
//        val registerItem = RegisterRecipeApiItem(recipeContainerApiItem = storyItem)
//        val bodyList: ArrayList<MultipartBody.Part> = ImageUtils.bitmapListToMultipartBodyPartList(storyImages,"story",context!!)
        val recipeService = RecipeService(activity!!, context!!, ServerAddress.serverAddress).service
        val call = recipeService.postEditStory(recipeId
            , StoryRegisterApiItem(content, referenceIdList)
            , ImageUtils.bitmapListToMultipartBodyPartList(storyImages,"story",context!!))
        call.enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                call.cancel()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()
                if(responseBody!=null){
                    Toast.makeText(context, "$responseBody", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    private fun register(content:String, storyImages: ArrayList<Bitmap>) {
        val service = RecipeService(activity!!, context!!, ServerAddress.serverAddress).service
        val call = service.postRegisterStory(user.id!!
            , StoryRegisterApiItem(content,referenceIdList)
            , ImageUtils.bitmapListToMultipartBodyPartList(storyImages,"story",context!!))
        call.enqueue(object: Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful && context!=null)
                    Toast.makeText(context, "스토리를 성공적으로 업로드 했습니다.", Toast.LENGTH_SHORT).show()
                else{
                    Toast.makeText(context, "스토리를 업로드 하지 못했습니다. 다시 새도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                call.cancel()
                Toast.makeText(context!!, "통신 실패로 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showSearchReferenceDialog(){
        referenceIdList.clear()
        for(ref in references){
            try{
                referenceIdList.add(ref.textView_referenceRecipe.tag.toString().toInt())
            }catch (e:Exception){}
        }
        val dialog = T3SearchReferenceDialog(referenceIdList)
        dialog.show(activity!!.supportFragmentManager, TAG_EVENT_DIALOG)
        dialog.setOnItemClickListener(object :T3SearchReferenceDialog.NoticeDialogListener{
            override fun onDialogPositiveClick(dialog: DialogFragment, reference: SearchReferenceApiItem?) {
                if (reference != null) {
                    targetView.textView_referenceRecipe.text = reference.recipeTitle
                    targetView.textView_referenceRecipe.tag = reference.id.toString()
                    targetView.layout_referenceRecipe.setBackgroundColor(ContextCompat.getColor(context!!,R.color.colorPrimary))
                }else{
                    Toast.makeText(context!! , "아이템이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) {
                Log.i(TAG, "canceled")
            }

        })
    }
    private fun hideKeyboard(){
        val inputMethodManager= activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(fragmentInflater.windowToken, 0)
    }
}
