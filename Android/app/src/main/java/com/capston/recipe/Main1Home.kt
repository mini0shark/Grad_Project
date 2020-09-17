package com.capston.recipe

import android.app.ProgressDialog
import android.content.Intent
import android.net.sip.SipErrorCode.TIME_OUT
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Adapter.T1HomeRecyclerViewAdapter
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.HompageApiItem
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_t1_home.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Main1Home : Fragment(), MainActivity.OnBackPressedListener{
    val TAG = "Main1Home Tag"
    lateinit var sft:SharedPreferenceTool
    lateinit var user:UserApiItem
    val homePageItem= arrayListOf<HompageApiItem>()
    internal lateinit var mAdapter: T1HomeRecyclerViewAdapter
    lateinit var bottomNav: BottomNavigationView
    lateinit var call:Call<ArrayList<HompageApiItem>>
    lateinit var fragmentInflater:View
    lateinit var progressDialog: ProgressDialog
    var isReadyToLoad= false
    var order = 0
    var trans = false
    val timeOut = 500
    var menu:Menu? = null
    var newMessage = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.i(TAG, "create")
    }

    override fun onCreateView(
        inflater: LayoutInflater,  container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity!! as MainActivity).setOnBackPressedListener(null) // HomeFragment에 왔을때 null로 바꿔준다.
        sft = SharedPreferenceTool(context!!)
        bottomNav = activity!!.findViewById(R.id.bottom_nav_view)
        val temp = sft.loadObjectSharedPreference<UserApiItem>(USER)
        if( temp != null){
            user = temp
        }else{
            sft.saveObjectSharedPreference(USER, null)
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
        fragmentInflater = inflater.inflate(R.layout.fragment_t1_home, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Home"

//        toolbarController()

        mAdapter = T1HomeRecyclerViewAdapter(context!!,activity!!, homePageItem, user, this)
        val homeRecyclerView = fragmentInflater.recyclerView_home
        homeRecyclerView.adapter = mAdapter
        homeRecyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL,false)
        homeRecyclerView .itemAnimator = DefaultItemAnimator()
        homeRecyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(1)){
                    Log.i(TAG, "진짜 끝!!!")
                    if(!trans){
                        service()
                        trans = true
                    }
                }
                else if(!recyclerView.canScrollVertically(-1)){
                    if(!trans){
                        if(isReadyToLoad){
                            order = 0
                            service()
                            trans = true
                            isReadyToLoad=false
                        }
                    }
                }
            }
        })
        progressDialog = ProgressDialog(context!!)
        progressDialog.setMessage("데이터 로딩중")
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE,"취소"
        ) { dialog, _ ->

        }
        // sharedPreference에 loadTime저장, HomeItem 저장해서 불러오던지 service ㄱㄱ
        order = 0
//        val loadItem = sft.loadObjectSharedPreference<ArrayList<HompageApiItem>>("homePageItem")
//        Log.i(TAG, "$loadItem")
//        if(loadItem!=null) {
//            homePageItem.addAll(loadItem)
//            order = ((homePageItem.size-1)/5)+1
//            mAdapter.notifyDataSetChanged()
//        }else
        service()
        (activity as MainActivity).setOnMainActivityControlListener(object : MainActivity.MainActivityController{
            override fun onReceiveMessage(flag:Boolean) {
                Log.i(TAG, "home!! $flag")
                try {
                    newMessage=(activity as MainActivity).flag
                }catch (e:Exception){
                    newMessage=false
                }

                if(menu!=null) {
                    if (newMessage) {
                        // 있으면
                        menu?.findItem(R.id.menuItem_homePage_message)?.setIcon(R.drawable.ic_question_red_24dp)
                    } else {
                        // 없으면
                        menu?.findItem(R.id.menuItem_homePage_message)?.setIcon(R.drawable.ic_question_black_24dp)
                    }
                }
            }

            override fun onReceiveMessage(json: JSONObject) {}
        })

        return fragmentInflater
    }
    override fun onResume() {
        super.onResume()
        if(bottomNav.visibility== View.GONE)
            bottomNav.visibility=View.VISIBLE
        while(findNavController().currentDestination!!.id!=R.id.home)
            findNavController().popBackStack()
    }
    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what === TIME_OUT) { // 타임아웃이 발생하면
                progressDialog.dismiss() // ProgressDialog를 종료
            }
        }
    }
    private fun service(){
        progressDialog.show()
//            .show(activity!!, "" , "데이터 로딩중")


        mHandler.sendEmptyMessageDelayed(timeOut, 50)
        Log.i(TAG, "Service function")
        val service = RecipeService(context = context!!, activity = activity!!, API_URL = ServerAddress.serverAddress).service
        call =service.getHomeListItems(user.userId!!, order++)
        call.enqueue(object: Callback<ArrayList<HompageApiItem>>{
            override fun onFailure(call: Call<ArrayList<HompageApiItem>>, t: Throwable) {
                trans = false
                // 데이터를 가져오는 것에 실패했습니다.
                call.cancel()
                Log.i(TAG, "Fail to service : \n\n${t.message}\n\n")
                if(context!=null)
                    Toast.makeText(context!!, "아이템을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                order--
            }

            override fun onResponse(call: Call<ArrayList<HompageApiItem>>, response: Response<ArrayList<HompageApiItem>>) {
                trans = false
                progressDialog.dismiss()
                Log.i(TAG, "dismiss")
                val responseHomeItems = response.body()
                if(responseHomeItems!=null){
                    if(responseHomeItems.size>0) {
                        if(order == 1) {
                            homePageItem.clear()
                        }
                        fragmentInflater.imageView_homeFragment_noItem.visibility = View.GONE
                        fragmentInflater.recyclerView_home.visibility = View.VISIBLE
                        homePageItem.addAll(responseHomeItems)
                        mAdapter.notifyDataSetChanged()
                    }else{
                        order--
                    }
                }
                else{
                    order--
                    Log.i(TAG, "받아온 아이템이 없습니다.")
                    fragmentInflater.imageView_homeFragment_noItem.visibility=View.VISIBLE
                }
            }
        })
        val loadTimer = Timer()
        loadTimer.schedule(object : TimerTask() {
            override fun run() {
                isReadyToLoad=true
            }

        }, 10000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (context as MainActivity).setOnBackPressedListener(this)
        if(!call.isCanceled)
            call.cancel()
        homePageItem.clear()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.i(TAG, "restore")
    }

    override fun onBack() {

    }
    override fun onBackWhenProgress() {
        Log.i(TAG, "back pressed")
        progressDialog.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu_home, menu)
        this.menu= menu
        newMessage=(activity as MainActivity).flag
        if(newMessage){
            Log.i(TAG, "createOption True")
            menu.findItem(R.id.menuItem_homePage_message).setIcon(R.drawable.ic_question_red_24dp)
        }else{
            Log.i(TAG, "createOption False")
            menu.findItem(R.id.menuItem_homePage_message).setIcon(R.drawable.ic_question_black_24dp)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuItem_homePage_message->{
                val bundle = Bundle()

                bottomNav.visibility= View.GONE
//                findNavController().navigate(R.id.action_home_to_chattingList, bundle)
                ////////////////
                bundle.putInt("userId", user.id!!)
                findNavController().navigate(R.id.action_home_to_chattingList, bundle)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
