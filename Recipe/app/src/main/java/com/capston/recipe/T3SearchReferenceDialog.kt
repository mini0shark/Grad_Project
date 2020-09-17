package com.capston.recipe

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Adapter.T3SearchReferenceRecyclerViewAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.SearchReferenceApiItem
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ConstCodes
import com.capston.recipe.Utils.GettingUser
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.ServerAddress
import kotlinx.android.synthetic.main.dialog_t3_search_reference.view.*
import kotlinx.android.synthetic.main.recycler_t23_search_reference_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class T3SearchReferenceDialog(val currentList:ArrayList<Int>) :DialogFragment(){
    val TAG = "SearchReferenceDialog"
    private lateinit var listener:NoticeDialogListener
    private val referenceItem = arrayListOf<SearchReferenceApiItem>()
    private lateinit var mAdapter: T3SearchReferenceRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var dialogInflater:View
    lateinit var user: UserApiItem
    private var selectedItem:SearchReferenceApiItem?=null
    lateinit var swithchButton:Switch
//    var selectedPosition = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogInflater = requireActivity().layoutInflater.inflate(R.layout.dialog_t3_search_reference, null)
        try {
            user = GettingUser.getUserInfo(context!!)!!
        }catch (e:NullPointerException){
            e.printStackTrace()
            GettingUser.sft.saveObjectSharedPreference(USER, null)
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
        viewSetting()
        switchButton()

        return activity?.let {
            val builder=AlertDialog.Builder(it)
            builder.setTitle("참조 레시피 검색")
                .setView(dialogInflater)
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { _, _ ->
                        listener.onDialogPositiveClick(this, selectedItem)
                    })
                .setNegativeButton("CANCEL",
                    DialogInterface.OnClickListener { _, _ ->
                        listener.onDialogNegativeClick(this)
                    })
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }

    private fun viewSetting() {
        mAdapter = T3SearchReferenceRecyclerViewAdapter(context!!, this, referenceItem, currentList)
        mAdapter.setOnItemClickListener(object: T3SearchReferenceRecyclerViewAdapter.OnReferenceClick{
            override fun onItemClick(reference: SearchReferenceApiItem) {
                if(reference.id !in currentList) {
                    dialogInflater.include_dialogSearchReference_SelectedItem
                        .textView_searchReferenceRecyclerItem_title.text = reference.recipeTitle
                    dialogInflater.include_dialogSearchReference_SelectedItem
                        .textView_searchReferenceRecyclerItem_foodName.text = reference.foodName
                    ImageUtils.putImageIntoView(context!!, reference.multiImageResult
                        , dialogInflater.include_dialogSearchReference_SelectedItem.imageView_searchReferenceImage)
                    dialogInflater.include_dialogSearchReference_SelectedItem
                        .textView_recyclerSearchReferenceItem_likeCount.text =
                        reference.likesCount.toString()
                    dialogInflater.include_dialogSearchReference_SelectedItem
                        .textView_recyclerSearchReferenceItem_writer.text = reference.user.nickname
                    selectedItem = reference
                }
            }

        })
        recyclerView = dialogInflater.recyclerView_SearchReferenceSearching
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        recyclerView .itemAnimator = DefaultItemAnimator()
        getRecipeListFromServer(null, ConstCodes.MINE)
    }

    fun setOnItemClickListener(listener: NoticeDialogListener){
        this.listener = listener
    }
    interface NoticeDialogListener{
        fun onDialogPositiveClick(dialog:DialogFragment, reference:SearchReferenceApiItem?)
        fun onDialogNegativeClick(dialog:DialogFragment)
    }

    private fun getRecipeListFromServer(searchText:String?, searchType:Int){
        if((searchText == null || searchText=="")&& searchType!=ConstCodes.MINE){
            Toast.makeText(context, "검색어를 입력하세요", Toast.LENGTH_SHORT).show()
        }else{
            val service = RecipeService(activity!!, context!!, ServerAddress.serverAddress).service
            var call= when(searchType){
                ConstCodes.MINE->{
                    service.getMyReferences(user.id!!)
                }
                else ->{
                    service.getReferences(searchText!!)
                }
            }
            call.enqueue(object : Callback<ArrayList<SearchReferenceApiItem>>{
                override fun onResponse(call: Call<ArrayList<SearchReferenceApiItem>>, response: Response<ArrayList<SearchReferenceApiItem>>) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.i(TAG, "res : $responseBody")
                        referenceItem.clear()
                        referenceItem.addAll(responseBody)
                        dialogInflater.textView_dialogSearchRef_background.hint = ""
                        mAdapter.notifyDataSetChanged()
                    }else{
                        Toast.makeText(context, "Recipe가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "not Exist..")
                    }
                }

                override fun onFailure(call: Call<ArrayList<SearchReferenceApiItem>>, t: Throwable) {
                    Log.i(TAG, "fail..")
                    t.printStackTrace()
                    call.cancel()
                }

            })
        }
    }

    private fun switchButton(){
        swithchButton = dialogInflater.switch_dialogSearchReference_mySearch
        swithchButton.setOnCheckedChangeListener { _, isChecked ->
            referenceItem.clear()
            when(isChecked){
                true->{
                    getRecipeListFromServer(null, ConstCodes.MINE)
                    dialogInflater.textView_dialogSearchRef_background.hint = "My Recipe"
                    Log.i(TAG, "true")
                    dialogInflater.linearLayout_SearchReference_forSearching.visibility=View.VISIBLE
                }
                false->{
                    dialogInflater.textView_dialogSearchRef_background.hint = "Search Recipe"
                    Log.i(TAG, "false")
                    dialogInflater.linearLayout_SearchReference_forSearching.visibility=View.GONE
                }
            }
        }
        dialogInflater.button_SearchReferenceSearching.setOnClickListener {
            val text = dialogInflater.editText_SearchReference_searchQuery.text.toString()
            getRecipeListFromServer(text, ConstCodes.SEARCH)
            hideKeyboard()
        }
    }
    private fun hideKeyboard(){
        val inputMethodManager= activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(dialogInflater.windowToken, 0)
    }

}