package com.capston.recipe


import android.app.ProgressDialog
import android.content.Intent
import android.net.sip.SipErrorCode
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Adapter.T2SearchPageRecyclerViewAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.RecipeContainerSimpleApiItem
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_t2_search.view.*
import org.threeten.bp.LocalDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class Main2Search : Fragment() {
    val TAG = "Main2Search Tag"
    lateinit var sft: SharedPreferenceTool
    lateinit var user: UserApiItem
    lateinit var recipeApiService: RecipeService
    lateinit var fragmentInflater:View
    lateinit var simpleRecipeItems:ArrayList<RecipeContainerSimpleApiItem>
    lateinit var mAdapter: T2SearchPageRecyclerViewAdapter
    lateinit var bottomNav: BottomNavigationView
    lateinit var progressDialog: ProgressDialog
    lateinit var call:Call<ArrayList<RecipeContainerSimpleApiItem>>
    var isReadyToLoad= false
    var order = 0
    var trans = false
    val timeOut = 500
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sft = SharedPreferenceTool(context!!)
        val temp = sft.loadObjectSharedPreference<UserApiItem>(USER)
        if( temp != null){
            user = temp
        }else{
            sft.saveObjectSharedPreference(USER, null)
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
        bottomNav = activity!!.findViewById(R.id.bottom_nav_view)
        fragmentInflater = inflater.inflate(R.layout.fragment_t2_search, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Search"

        setHasOptionsMenu(true)
        simpleRecipeItems = arrayListOf()
        mainRecyclerView()
        getRecipeItem()



        return fragmentInflater
    }
    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what === SipErrorCode.TIME_OUT) { // 타임아웃이 발생하면
                progressDialog.dismiss() // ProgressDialog를 종료
            }
        }
    }
    private fun getRecipeItem(){
        mHandler.sendEmptyMessageDelayed(timeOut, 500)

        recipeApiService = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        call = recipeApiService.service.getRecipeSearchItems(user.id, order++)
        call.enqueue(object : Callback<ArrayList<RecipeContainerSimpleApiItem>> {
            override fun onResponse(call: Call<ArrayList<RecipeContainerSimpleApiItem>>, response: Response<ArrayList<RecipeContainerSimpleApiItem>>) {
                trans = false
                progressDialog.dismiss()
                val responseBody = response.body()
                if(responseBody!=null) {
                    if(responseBody.size>0){
                        if(order == 1)
                            simpleRecipeItems.clear()
                        simpleRecipeItems.addAll(responseBody)
                        Log.i(TAG, simpleRecipeItems.toString())
                        mAdapter.notifyDataSetChanged()
                    }else{
                        order--
                    }
                }
                else{
                    order--
                }
            }

            override fun onFailure(call: Call<ArrayList<RecipeContainerSimpleApiItem>>, t: Throwable) {
                // 데이터를 가져오는 것에 실패했습니다.
                trans = false
                call.cancel()
                progressDialog.dismiss()
                order--
                Log.i(TAG, "Fail to service : \n\n${t.message}\n\n")
                // Toast 메세지
            }

        })
        val loadTimer = Timer()
        loadTimer.schedule(object : TimerTask() {
            override fun run() {
                isReadyToLoad=true
            }

        }, 10000)

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuItem_search->{
                findNavController().navigate(R.id.action_search_to_searchDetail)
                bottomNav.visibility = View.GONE
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun mainRecyclerView(){
        mAdapter = T2SearchPageRecyclerViewAdapter(context!!,activity!!, this, simpleRecipeItems)
        val searchRecyclerView = fragmentInflater.recyclerView_searchPage
//        loadTime = LocalDateTime.now()
        searchRecyclerView .adapter = mAdapter
        searchRecyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL,false)
        searchRecyclerView .itemAnimator = DefaultItemAnimator()
        searchRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(1)){
                    if(!trans){
                        Log.i(TAG, "끝")
                        getRecipeItem()
                        trans = true
                    }
                }
                else if(!recyclerView.canScrollVertically(-1)){
                    if(!trans){
                        if(isReadyToLoad){
                            order = 0
                            getRecipeItem()
                            trans = true
                            isReadyToLoad=false

                        }
                    }
                }
            }
        })
        progressDialog = ProgressDialog(context!!)
        progressDialog.setMessage("데이터 로딩중")
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE,"취소") { dialog, _ ->

        }
        order = 0
    }
    override fun onResume() {
        super.onResume()
        if(bottomNav.visibility== View.GONE)
            bottomNav.visibility=View.VISIBLE
        while(findNavController().currentDestination!!.id!=R.id.search)
            findNavController().popBackStack()

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "save==================")
    }
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.i(TAG, "restore")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if(!call.isCanceled)
            call.cancel()
    }
}
