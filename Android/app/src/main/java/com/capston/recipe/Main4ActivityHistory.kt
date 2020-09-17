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
import com.capston.recipe.Items.HistoryApiItem
import com.capston.recipe.Adapter.T4HistoryItemsRecyclerViewAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_t4_activity_history.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Main4ActivityHistory : Fragment() {
    val TAG = "Main4History Tag"
    lateinit var sft: SharedPreferenceTool
    lateinit var user: UserApiItem
    lateinit var bottomNav: BottomNavigationView
    lateinit var historyList:ArrayList<HistoryApiItem>
    lateinit var mAdapter:T4HistoryItemsRecyclerViewAdapter
    lateinit var call:Call<ArrayList<HistoryApiItem>>
    lateinit var fragmentInflater:View
    lateinit var historyRecyclerView: RecyclerView
    lateinit var progressDialog: ProgressDialog
    var order = 0
    var trans = false
    val timeOut = 500

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInflater = inflater.inflate(R.layout.fragment_t4_activity_history, container, false)
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
        (activity as AppCompatActivity).supportActionBar?.title = "History"
        Log.i(TAG, "createView")
        setHasOptionsMenu(true)
        initHistoryAdapter()

        bottomNav = activity!!.findViewById(R.id.bottom_nav_view)

        return fragmentInflater
    }
    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what === SipErrorCode.TIME_OUT) { // 타임아웃이 발생하면
                progressDialog.dismiss() // ProgressDialog를 종료
            }
        }
    }
    private fun getHistoryItem(){
        progressDialog.show()
        val recipeApiService = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
        mHandler.sendEmptyMessageDelayed(timeOut, 50)
        call=recipeApiService.service.getMyHistory(user.id!!, order++)
        call.enqueue(object :Callback<ArrayList<HistoryApiItem>>{
            override fun onResponse(call: Call<ArrayList<HistoryApiItem>>, response: Response<ArrayList<HistoryApiItem>>) {
                val responseBody = response.body()
                Log.e(TAG, "history request received")
                trans = false
                progressDialog.dismiss()
                if(responseBody !=null){
                    if(responseBody.size>0) {
                        if(order==1)
                            historyList.clear()
                        historyList.addAll(responseBody)
                        mAdapter.notifyDataSetChanged()
                    }else
                        order--
                }else{
                    Log.e(TAG, "history request fail")
                    order--
                }
            }

            override fun onFailure(call: Call<ArrayList<HistoryApiItem>>, t: Throwable) {
                Log.e(TAG, "fail")
                trans = false
                progressDialog.dismiss()
                order--
                call.cancel()
                t.printStackTrace()
            }

        })
    }
    private fun initHistoryAdapter(){
        historyList = arrayListOf()
        historyRecyclerView = fragmentInflater.recyclerView_history
        mAdapter = T4HistoryItemsRecyclerViewAdapter(context!!, activity!!, this, historyList)
        historyRecyclerView.adapter = mAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL,false)
        historyRecyclerView.itemAnimator = DefaultItemAnimator()
        historyRecyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(1)){
                    if(!trans){
                        getHistoryItem()
                        trans = true
                    }
                }
            }

        })
        progressDialog = ProgressDialog(context!!)
        progressDialog.setMessage("데이터 로딩중")
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE,"취소"
        ) { dialog, _ ->

        }
        getHistoryItem()
    }

    override fun onResume() {
        super.onResume()
        while(findNavController().currentDestination!!.id!=R.id.activityHistory){
            findNavController().popBackStack()
        }
        if(bottomNav.visibility== View.GONE)
            bottomNav.visibility=View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "destroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        order=0
        Log.i(TAG, "destroyview")
    }


}
