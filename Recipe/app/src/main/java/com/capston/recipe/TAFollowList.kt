package com.capston.recipe


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Adapter.TAFollowListRecyclerAdapter
import com.capston.recipe.Api.userApi.UserService
import com.capston.recipe.Items.FollowUserApiItem
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ConstCodes.FOLLOWER
import com.capston.recipe.Utils.ConstCodes.FOLLOWING
import com.capston.recipe.Utils.ConstCodes.WEEKS_FOLLOWER
import com.capston.recipe.Utils.GettingUser
import com.capston.recipe.Utils.ServerAddress
import kotlinx.android.synthetic.main.fragment_ta_follow_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TAFollowList : Fragment() {
    lateinit var fragmentInflater:View
    lateinit var recyclerView:RecyclerView
    lateinit var mAdapter: TAFollowListRecyclerAdapter
    val followList = arrayListOf<FollowUserApiItem>()
    var userId:Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userId = arguments?.getInt("userId")!!
        fragmentInflater = inflater.inflate(R.layout.fragment_ta_follow_list, container, false)
        recyclerView = fragmentInflater.recyclerView_followList
        mAdapter = TAFollowListRecyclerAdapter(context!!, this,  followList)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        recyclerView .itemAnimator = DefaultItemAnimator()
        val service = UserService(activity!!, context!!, ServerAddress.serverAddress).service
        var call:Call<ArrayList<FollowUserApiItem>>? = null
        when (arguments?.getInt("type")) {
            FOLLOWER -> {
                (activity as AppCompatActivity).supportActionBar?.title = "팔로워"
                call = service.getFollowerList(userId)
            }
            FOLLOWING -> {
                (activity as AppCompatActivity).supportActionBar?.title = "팔로잉"
                call = service.getFollowingList(userId)
            }
            WEEKS_FOLLOWER->{
                (activity as AppCompatActivity).supportActionBar?.title = "신규 팔로워"
                call = service.getWeeksFollowerList(userId)
            }
            else -> {
                findNavController().popBackStack()
            }
        }
        call?.enqueue(object : Callback<ArrayList<FollowUserApiItem>>{
            override fun onResponse( call: Call<ArrayList<FollowUserApiItem>>, response: Response<ArrayList<FollowUserApiItem>>) {
                val responseBody = response.body()
                if(response.isSuccessful){
                    followList.clear()
                    if (responseBody != null) {
                        followList.addAll(responseBody)
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<FollowUserApiItem>>, t: Throwable) {
            }

        })
        return fragmentInflater
    }

    override fun onResume() {
        super.onResume()

        while(findNavController().currentDestination!!.id!=R.id.followList)
            findNavController().popBackStack()
    }
}
