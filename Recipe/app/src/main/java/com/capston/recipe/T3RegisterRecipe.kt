package com.capston.recipe

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capston.recipe.Adapter.T3ImageViewPagerRegisterAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.*
import com.capston.recipe.Utils.ConstCodes.EDIT
import com.capston.recipe.Utils.GettingUser
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_t3_register_recipe.*
import kotlinx.android.synthetic.main.fragment_t3_register_recipe.view.*
import kotlinx.android.synthetic.main.layout_t3_extra_tips.view.*
import kotlinx.android.synthetic.main.layout_t3_ingredients.view.*
import kotlinx.android.synthetic.main.layout_t3_recipe.view.*
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class T3RegisterRecipe : Fragment() {
    var forPerson= 0
    val ingredientsList = arrayListOf<View>()
    val recipeOrderList = arrayListOf<View>()
    val extraTipList = arrayListOf<View>()
    lateinit var spinnerItem:String
    lateinit var user:UserApiItem
    lateinit var bottomNav: BottomNavigationView
    lateinit var blankCall: Call<RecipeDetailApiItem>
    var requestRecipeNumber = -1
    var recipeId = -1

    lateinit var fragmentInflater:View
    //    private var addImageList = arrayListOf<AddImageItem>()
    internal var imageList = arrayListOf<Bitmap>()
    val imagePathList= arrayListOf<String?>()
    lateinit var mAdapter: T3ImageViewPagerRegisterAdapter
    lateinit var sft:SharedPreferenceTool
    val TAG = "RegisterRecipe tag"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

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

        fragmentInflater = inflater.inflate(R.layout.fragment_t3_register_recipe, container, false)

        val approachRoot = arguments?.getInt("approachRoot")
        if(approachRoot!=null){
            if(approachRoot== EDIT){
                (activity as AppCompatActivity).supportActionBar?.title = "수정 - Recipe"
                settingBlank(inflater, container)
            }
            else{
                (activity as AppCompatActivity).supportActionBar?.title = "등록 - Recipe"
                addIngredientsLayout(inflater, container)
                addRecipeLayout(inflater, container)
            }
        }
        settingKeyboard()
        classificationController()
        requiredTime()
        buttonControl(inflater, container)
        mAdapter = T3ImageViewPagerRegisterAdapter(context!!, imageList, this,imagePathList)
        val imageViewPager = fragmentInflater.viewPager_completeFoods
        imageViewPager.clipToPadding = false
        imageViewPager.adapter = mAdapter



        when (val id = this.findNavController().currentDestination!!.id) {
            R.id.registerRecipe -> {
                Log.i("tag", "addStory to~~")
            }
            R.id.addStory -> {
                Log.i("tag","add story")
            }
            else -> {
                Log.i("tag", "$id")
            }
        }

        return fragmentInflater
    }

    private fun settingBlank(inflater: LayoutInflater, container: ViewGroup?) {
        recipeId =arguments?.getInt("recipeId")!!
        fragmentInflater.textView_registerRecipe_registerText.text = "수정하기"
        fragmentInflater.button_registerRecipe.text = "수정"

        val service = RecipeService(activity!!, context!!, ServerAddress.serverAddress).service
        blankCall = service.getRecipe(recipeId!!)
        blankCall.enqueue(object :Callback<RecipeDetailApiItem>{
            override fun onResponse(call: Call<RecipeDetailApiItem>, response: Response<RecipeDetailApiItem>) {
                val responseBody= response.body()
                if(responseBody != null){
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
                    fragmentInflater.editText_recipeTitle.setText(responseBody.recipeTitle.toString())
                    fragmentInflater.editText_foodName.setText(responseBody.foodName.toString())
                    fragmentInflater.editText_introduceRecipe.setText(responseBody.introduceRecipe.toString())
                    val selection = when(responseBody.category){
                        "한식"->0
                        "일식"->1
                        "중식"->2
                        "양식"->3
                        "분식"->4
                        "디저트"->5
                        "아시아음식"->6
                        "도시락"->7
                        "피자/햄버거"->8
                        "소스류"->9
                        else->10
                    }
                    fragmentInflater.spinner_category.setSelection(selection)
                    try {
                        fragmentInflater.textView_forPerson.text = responseBody.forPerson!!.toInt().toString()
                        forPerson = responseBody.forPerson!!.toInt()
                    }catch (e:Exception){
                        fragmentInflater.textView_forPerson.text = "0"
                        forPerson = 0
                    }
                    try {
                        fragmentInflater.editText_requiredTime.setText(responseBody.requiredTime!!.toInt().toString())
                    }catch (e:Exception){
                        fragmentInflater.editText_requiredTime.setText("0")
                    }
                    ingredientsList.clear()
                    if(responseBody.ingredients!=null) {
                        for (ingre in responseBody.ingredients!!) {
                            addIngredientsLayoutWithItem(inflater, container, ingre)
                        }
                    }
                    if(responseBody.recipeOrder!=null){
                        for(recipe in responseBody.recipeOrder!!){
                            addRecipeLayoutWithItem(inflater, container, recipe)
                        }
                    }
                    if(responseBody.extraTip!=null){
                        for(tips in responseBody.extraTip!!){
                            addExtraTipsWithItemLayout(inflater, container, tips)
                        }
                    }


                }
            }

            override fun onFailure(call: Call<RecipeDetailApiItem>, t: Throwable) {
                Toast.makeText(context!!, "통신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                fragmentManager!!.popBackStack()
            }

        })
    }
    private fun addIngredientsLayoutWithItem(inflater:LayoutInflater, container: ViewGroup?, ingredients:IngredientApiItem) {
        val addIngredientsLayout =
            inflater.inflate(R.layout.layout_t3_ingredients, container, false)
        addIngredientsLayout.editText_ingredient.setText(ingredients.ingredient)
        addIngredientsLayout.editText_amount.setText(ingredients.amount)
        fragmentInflater.linearLayout_ingredients.addView(addIngredientsLayout)
        ingredientsList.add(addIngredientsLayout)
    }
    private fun addRecipeLayoutWithItem(inflater:LayoutInflater, container: ViewGroup?, recipeItem:RecipeApiItem){
        val addRecipeLayout = inflater.inflate(R.layout.layout_t3_recipe, container, false)
        addRecipeLayout.textView_recipeOrder.text = (recipeOrderList.size+1).toString()
        ImageUtils.putImageIntoView(context!!, recipeItem.image, addRecipeLayout.imageView_recipeOrder)
        addRecipeLayout.editText_recipeOrder.setText(recipeItem.explain)

        fragmentInflater.linearLayout_recipe.addView(addRecipeLayout)
        recipeOrderList.add(addRecipeLayout)
        addRecipeLayout.imageView_recipeOrder.setOnClickListener {

            addRecipeLayout.imageView_recipeOrder.setOnClickListener {
                val imageAddFragment = EditSingleImageFragment()
                val transaction = fragmentManager!!.beginTransaction()
                transaction.add(R.id.frameLayout_registerRecipe, imageAddFragment, "edit")
                transaction.addToBackStack(null)
                transaction.commit()
                fragmentInflater.frameLayout_registerRecipe.visibility = View.VISIBLE
                imageAddFragment.setDoneClickListener(object : EditSingleImageFragment.DoneClickListener{
                    override fun onClickDone(imgList: java.util.ArrayList<Bitmap>) {
                        fragmentInflater.frameLayout_registerRecipe.visibility = View.GONE
                        for(img in imgList)
                            fragmentInflater.imageView_recipeOrder.setImageBitmap(img)
                    }

                    override fun onCancel() {
                        (activity!! as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        fragmentInflater.frameLayout_registerRecipe.visibility = View.GONE
                        fragmentInflater.scrollView_registerRegister.visibility = View.VISIBLE
                    }

                })
            }
        }
    }
    private fun addExtraTipsWithItemLayout(inflater:LayoutInflater, container: ViewGroup?, extraTip: ExtraTip){
        val addExtraTipsLayout = inflater.inflate(R.layout.layout_t3_extra_tips, container, false)
        addExtraTipsLayout.editText_extraTip.setText(extraTip.tip)
        fragmentInflater.linearLayout_extraTips.addView(addExtraTipsLayout)
        extraTipList.add(addExtraTipsLayout)
    }

    private fun settingKeyboard(){
        fragmentInflater.linearLayout_registerRecipe.setOnClickListener {
            hideKeyboard()
        }

    }
    private fun hideKeyboard(){
        val inputMethodManager= activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)
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
        fragmentInflater.frameLayout_registerRecipe.visibility=View.VISIBLE
        val imageAddFragment = EditMultipleImageFragment()
        val transaction = fragmentManager!!.beginTransaction()
        transaction.add(R.id.frameLayout_registerRecipe, imageAddFragment, "edit")
        transaction.addToBackStack(null)
        transaction.commit()
        fragmentManager!!.addOnBackStackChangedListener {

        }
        imageAddFragment.setDoneClickListener(object:EditMultipleImageFragment.DoneClickListener{
            override fun onClickDone(imgList: ArrayList<Bitmap>) {
                imageList.clear()
                imageList.addAll(imgList)
                imagePathList.clear()
                mAdapter.notifyDataSetChanged()
            }

            override fun onCancel() {
                (activity!! as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
                fragmentInflater.frameLayout_registerRecipe.visibility = View.GONE
                fragmentInflater.scrollView_registerRegister.visibility = View.VISIBLE
            }

        })
    }
    private fun buttonControl(inflater:LayoutInflater, container: ViewGroup?){

        fragmentInflater.button_addCompleteImages.setOnClickListener {
            if(imageList.size>0){
                alertHandler()
                // fix - alert dialog\ 해서 기존이미지를 다 지우고 다시 등록할 건지 물어보기
            }else{
                multipleImageFragmentStarter()
            }
        }

        fragmentInflater.imageButton_addIngredients.setOnClickListener {
            addIngredientsLayout(inflater,container)
        }
        fragmentInflater.imageButton_removeIngredients.setOnClickListener {
            if(ingredientsList.size>1){
                val index = ingredientsList.size-1
                fragmentInflater.linearLayout_ingredients.removeView(ingredientsList[index])
                ingredientsList.removeAt(index)
            }else{
                Toast.makeText(context!!, "하나이상의 재료는 필수 입니다.", Toast.LENGTH_SHORT).show()
            }
//            for(ll in ingredientsList){
//                Log.i("tag", "${ll.findViewById<EditText>(R.id.editText_ingredient)}")
//            }
        }

        fragmentInflater.imageButton_addRecipe.setOnClickListener {
            addRecipeLayout(inflater, container)
        }
        fragmentInflater.imageButton_removeRecipe.setOnClickListener {
            if(recipeOrderList.size>0){
                val index = recipeOrderList.size-1
                fragmentInflater.linearLayout_recipe.removeView(recipeOrderList[index])
                recipeOrderList.removeAt(index)
            }
        }

        fragmentInflater.imageButton_addExtraTips.setOnClickListener {
            addExtraTipsLayout(inflater, container)
        }
        fragmentInflater.imageButton_removeExtraTips.setOnClickListener {
            if(extraTipList.size>0){
                val index = extraTipList.size-1
                fragmentInflater.linearLayout_extraTips.removeView(extraTipList[index])
                extraTipList.removeAt(index)
            }
        }

        fragmentInflater.button_registerRecipe.setOnClickListener {
            registerRecipe(fragmentInflater)
        }

    }
    private fun registerRecipe(fragmentInflater: View){
        val imageName = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(Date(System.currentTimeMillis()))
        val recipeTitle = fragmentInflater.editText_recipeTitle.text.toString()
        var foodName = if(!fragmentInflater.editText_foodName.text.toString().isNullOrEmpty()){
            fragmentInflater.editText_foodName.text.toString()
        }else
            recipeTitle
        val introduceRecipe = fragmentInflater.editText_introduceRecipe.text.toString()
        val category = spinnerItem
        val requiredTime = editText_requiredTime.text.toString().toInt()
        val ingredients = arrayListOf<IngredientApiItem>()
        val recipe = arrayListOf<RecipeApiItem>()
        val extraTips = arrayListOf<ExtraTip>()
        Log.i(TAG,"${imageList.size}, ${imagePathList.size}")

        when {
            imagePathList.size==0 && imageList.size<1->Toast.makeText(context!!, "적어도 한장의 사진이 필요합니다." , Toast.LENGTH_SHORT).show()
            recipeTitle=="" ->Toast.makeText(context!!, "레시피 제목을 입력해 주세요" , Toast.LENGTH_SHORT).show()
            introduceRecipe=="" -> Toast.makeText(context!!, "레시피를 소개해 주세요" , Toast.LENGTH_SHORT).show()
            category=="카테고리" ->Toast.makeText(context!!, "카테고리를 분류하지 않았습니다." , Toast.LENGTH_SHORT).show()
            recipeOrderList.size<1 -> Toast.makeText(context!!, "한개 이상의 레시피가 필요합니다." , Toast.LENGTH_SHORT).show()
            requiredTime < 1 -> Toast.makeText(context!!, "소요시간을 입력해주세요" , Toast.LENGTH_SHORT).show()
            ingredientsList.size < 1 -> Toast.makeText(context!!, "파란색 \"+\"를 눌라재료를 입력해 주세요" , Toast.LENGTH_SHORT).show()
            else -> {
                val bodyList= ArrayList<MultipartBody.Part>()
                if(imagePathList.size==0)
                    for(i in 0 until imageList.size){
                        val resultImageName = imageName+"_complete_$i"
                        val imageFile:File = getFileFromBitmap(imageList[i], resultImageName)
                        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
                        val multipartBody= MultipartBody.Part.createFormData("complete_${i+1}", imageFile.name, requestBody)
                        bodyList.add(multipartBody)
                    }
                for(ingredient in ingredientsList){
                    val ing = ingredient.editText_ingredient.text.toString()
                    val amt = ingredient.editText_amount.text.toString()
                    if(ing !="") {
                        ingredients.add(IngredientApiItem(null, ing, amt))
                    }
                }
                Log.i("RegisterRecipe", "${recipeOrderList.size}")
                for(recipeOrder in recipeOrderList){
                    val recipeIntroduce = recipeOrder.editText_recipeOrder.text.toString()
                    val recipeImage = if(recipeOrder.imageView_recipeOrder.drawable!=null) recipeOrder.imageView_recipeOrder.drawable.toBitmap()
                    else ContextCompat.getDrawable(context!!, R.drawable.sample)!!.toBitmap()
                    val order = recipeOrder.textView_recipeOrder.text.toString()
                    if (!order.isNullOrEmpty()) {
                        val recipeImageName = imageName+"_recipe_$order"
                        recipe.add(RecipeApiItem(null, order.toInt(), recipeImageName, recipeIntroduce))
                        Log.i("RegisterRecipe", "recipe : $recipe")
                        val imageFile:File = getFileFromBitmap(recipeImage, recipeImageName)
//                                val imageFile:File = getFileFromBitmap(recipeImage!!, recipeImageName)
                        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
                        val multiPartBody= MultipartBody.Part.createFormData("recipe_$order", imageFile.name, requestBody)
                        bodyList.add(multiPartBody)
//                            bodyMap["recipe$order"]=multipartBody
                    }else{
                        Log.i("RegisterRecipe", "recipe : null or empty")
                    }

                }
                if(extraTipList.size>0){
                    for(tip in extraTipList) {
                        val t = tip.editText_extraTip.text.toString()
                        Log.i("registerRecipe", "$t")
                        if(t != "") {
                            extraTips.add(ExtraTip(tip=t))
                        }
                    }
                }
                val recipeContainer = RecipeContainerApiItem(null,
                    recipeTitle, foodName , introduceRecipe, category, forPerson, requiredTime
                    , null, true)

                val registerRecipeApiItem = RegisterRecipeApiItem(recipeContainer,ingredients, recipe, extraTips)
                Log.i("RegisterRecipe", registerRecipeApiItem.toString())
                val approachRoot = arguments?.getInt("approachRoot")
                if(approachRoot== EDIT){
                    edit(registerRecipeApiItem, bodyList)
                }else{
                    register(registerRecipeApiItem, bodyList)
                }

                fragmentManager!!.popBackStack()
            }
        }
    }
    private fun edit(registerRecipeApiItem:RegisterRecipeApiItem, bodyList:ArrayList<MultipartBody.Part>){
        val recipeService = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        recipeService.patchRegisterRecipeToServer(recipeId,registerRecipeApiItem, bodyList, fragmentManager!!)

    }
    private fun register(registerRecipeApiItem:RegisterRecipeApiItem, bodyList:ArrayList<MultipartBody.Part>){
        val recipeService = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        recipeService.postRegisterRecipeToServer(user.id!!,registerRecipeApiItem, bodyList, fragmentManager!!)

    }
    private fun getFileFromBitmap(bitmap:Bitmap, name:String):File{
        val filesDir = context!!.filesDir
        val file =File(filesDir, "${name}.png")
        val os:OutputStream
        try{
            os = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        }catch (e:Exception){
            Log.e(javaClass.simpleName, "Error writing bitmap ",e)
        }
        return file
    }
    private fun addIngredientsLayout(inflater:LayoutInflater, container: ViewGroup?){
        val addIngredientsLayout = inflater.inflate(R.layout.layout_t3_ingredients, container, false)
        fragmentInflater.linearLayout_ingredients.addView(addIngredientsLayout)
        ingredientsList.add(addIngredientsLayout)
    }
    private fun addExtraTipsLayout(inflater:LayoutInflater, container: ViewGroup?){
        val addExtraTipsLayout = inflater.inflate(R.layout.layout_t3_extra_tips, container, false)
        fragmentInflater.linearLayout_extraTips.addView(addExtraTipsLayout)
        extraTipList.add(addExtraTipsLayout)
    }
    private fun addRecipeLayout(inflater:LayoutInflater, container: ViewGroup?){
        val addRecipeLayout = inflater.inflate(R.layout.layout_t3_recipe, container, false)
        fragmentInflater.linearLayout_recipe.addView(addRecipeLayout)
        recipeOrderList.add(addRecipeLayout)
        addRecipeLayout.textView_recipeOrder.text = recipeOrderList.size.toString()

        addRecipeLayout.imageView_recipeOrder.setOnClickListener {
            val imageAddFragment = EditSingleImageFragment()
            val transaction = fragmentManager!!.beginTransaction()
            transaction.add(R.id.frameLayout_registerRecipe, imageAddFragment, "edit")
            transaction.addToBackStack(null)
            transaction.commit()
            fragmentInflater.frameLayout_registerRecipe.visibility = View.VISIBLE
            imageAddFragment.setDoneClickListener(object : EditSingleImageFragment.DoneClickListener{
                override fun onClickDone(imgList: java.util.ArrayList<Bitmap>) {
                    fragmentInflater.frameLayout_registerRecipe.visibility = View.GONE
                    for(img in imgList)
                        addRecipeLayout.imageView_recipeOrder.setImageBitmap(img)
                }

                override fun onCancel() {
                    (activity!! as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    fragmentInflater.frameLayout_registerRecipe.visibility = View.GONE
                    fragmentInflater.scrollView_registerRegister.visibility = View.VISIBLE
                }

            })
        }
    }
    private fun classificationController(){

        val spinnerItemLIst = resources.getStringArray(R.array.food_category)
        val spinnerAdapter = ArrayAdapter(context!!,android.R.layout.simple_spinner_dropdown_item, spinnerItemLIst)
        fragmentInflater.spinner_category.adapter = spinnerAdapter
        fragmentInflater.spinner_category.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerItem = parent!!.getItemAtPosition(position).toString()
            }

        }


        forPerson = fragmentInflater.textView_forPerson.text.toString().toInt()
        fragmentInflater.button_forPersonLeft.setOnClickListener {
            if(forPerson>0){
                forPerson--
                fragmentInflater.textView_forPerson.text = forPerson.toString()
            }
        }
        fragmentInflater.button_forPersonRight.setOnClickListener {
            forPerson++
            fragmentInflater.textView_forPerson.text = forPerson.toString()
        }
    }
    private fun requiredTime(){
        fragmentInflater.editText_requiredTime.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val editTextString = fragmentInflater.editText_requiredTime.text.toString()
                if(editTextString != "") {
                    val requiredTimeHour: Float = (editTextString.toFloat())/60
                    fragmentInflater.textView_requiredTime.text =
                        String.format("%.1f", requiredTimeHour)
                }else{
                    fragmentInflater.textView_requiredTime.text =
                        String.format("0.0")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

    }
    override fun onDestroyView() {
        super.onDestroyView()
        val approachRoot = arguments?.getInt("approachRoot")
        if(approachRoot == EDIT)
            if(!blankCall.isCanceled)
                blankCall.cancel()
    }
}
