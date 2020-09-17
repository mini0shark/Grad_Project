package com.capston.recipe


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.capston.recipe.Api.chatApi.ChatService
import com.capston.recipe.Items.ChattingListApiItem
import com.capston.recipe.Items.UserForSimpleDisplyApiItem
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.ServerAddress
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_chatting_list.view.*
import kotlinx.android.synthetic.main.fragment_chatting_list.view.layout_chattingList
import kotlinx.android.synthetic.main.layout_chatting_list_item.view.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ChattingList : Fragment() {
    lateinit var fragmentInflater: View
    lateinit var call: Call<ArrayList<ChattingListApiItem>>
    lateinit var inflater: LayoutInflater
    var container: ViewGroup?=null
    var userId=0
    val TAG = "ChattingList"
    lateinit var chatRoomList:HashMap<Int, View>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentInflater = inflater.inflate(R.layout.fragment_chatting_list, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "대화목록"
        try{
            userId = arguments?.getInt("userId")!!
            chatRoomList = hashMapOf()
            getChattingRoomList()
            (activity as MainActivity).setOnMainActivityControlListener(object : MainActivity.MainActivityController{
                override fun onReceiveMessage(flag: Boolean) {}

                override fun onReceiveMessage(json: JSONObject) {
                    val gson = Gson()

                    val messageList =
                        gson.fromJson(json.getString("messageList"), Array<ChattingListApiItem>::class.java)
                    for( item in messageList){
                        Log.i(TAG,"massesasdkfjdskdjd  : ${item.message}")
                        if(chatRoomList.containsKey(item.room)){
                            val view = chatRoomList[item.room]
                            fragmentInflater.layout_chattingList.removeView(view)
                            chatRoomList.remove(item.room)
                            if(item.user_list!=null)
                                for (usr in item.user_list)
                                    if (userId != usr.id)
                                        displayMsg(item, usr)
                        }
                    }

                }

            })
            this.container = container
            this.inflater = inflater
        }catch (e:Exception){
            findNavController().popBackStack()
        }
        return fragmentInflater
    }

    override fun onResume() {
        super.onResume()

        while(findNavController().currentDestination!!.id!=R.id.chattingList)
            findNavController().popBackStack()
    }
    fun getChattingRoomList(){
        val service = ChatService(ServerAddress.serverAddress).service
        call =service.getChattingList(userId)
        call.enqueue(object : Callback<ArrayList<ChattingListApiItem>>{
            override fun onResponse(call: Call<ArrayList<ChattingListApiItem>>, response: Response<ArrayList<ChattingListApiItem>>) {
                val responseBody = response.body()
                if(responseBody!=null){

                    for(lst in responseBody) {
                        var user:UserForSimpleDisplyApiItem
                        if (lst.user_list != null)
                            for (usr in lst.user_list)
                                if (userId != usr.id)
                                    displayMsg(lst, usr)
                        Log.i(TAG,"getChattingList ${lst.message}")
                    }
//                    responseControl(responseBody)
                }
            }

            override fun onFailure(call: Call<ArrayList<ChattingListApiItem>>, t: Throwable) {
                t.printStackTrace()
                call.cancel()
            }

        })
    }
    fun displayMsg(chattingApi:ChattingListApiItem, usr:UserForSimpleDisplyApiItem){
        if(chattingApi.message!=null) {
            var chatRoom:View = inflater.inflate(R.layout.layout_chatting_list_item, container, false)
            if(chattingApi.type==1)
                chatRoom.textView_chattingList_mainMessage.text = "새로운 질문!!"
            else
                chatRoom.textView_chattingList_mainMessage.text = chattingApi.message
            val roomId = chattingApi.room
            if(!chattingApi.check){
                val service = ChatService(ServerAddress.serverAddress).service
                val call =service.getIsNew(chattingApi.id, userId)
                call.enqueue(object:Callback<Boolean>{
                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    }

                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        val responseBody = response.body()
                        if(responseBody!=null){
                            if(responseBody)
                                chatRoom.imageView_chattingList_new.visibility=View.VISIBLE
                        }
                    }

                })
            }else{
                chatRoom.imageView_chattingList_new.visibility=View.INVISIBLE
            }
            chatRoom.textView_chattingList_user.text = usr.nickname
            ImageUtils.putImageIntoCircleView(context!!, usr.profileImage, chatRoom.imageView_chattingList)
            chatRoom.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("opponentUserId", usr.id)
                findNavController().navigate(R.id.action_chattingList_to_chatting, bundle)
                (activity as MainActivity).checkNewMessage(usr.id)
            }
            chatRoomList[roomId] = (chatRoom)
            fragmentInflater.layout_chattingList.addView(chatRoom)
        }
    }
//    fun responseControl(responseItems:ArrayList<ChattingListApiItem>){
//        for(item in responseItems){
//            val newView = inflater.inflate(R.layout.layout_chatting_list_item, container,false)
//            val bundle = Bundle()
//            if(item.user!=null) {
//                for (chatItem in item.user!!) {
//                    if (userId != chatItem.id) {
//                        bundle.putInt("opponentUserId", chatItem.id)
//                        fragmentInflater.textView_chattingList_user.text = chatItem.nickname
//                        ImageUtils.putImageIntoCircleView(context!!, chatItem.profileImage, fragmentInflater.imageView_chattingList)
//                    }
//                }
//            }
//            else{
//                fragmentInflater.textView_chattingList_user.text = "탈퇴한 계정"
//                ImageUtils.putImageIntoCircleView(context!!, ContextCompat.getDrawable(context!!, R.drawable.sample)!!, fragmentInflater.imageView_chattingList)
//            }
//            fragmentInflater.textView_chattingList_mainMessage
//            newView.setOnClickListener {
//                findNavController().navigate(R.id.action_chattingList_to_chatting,bundle)
//            }
//            fragmentInflater.layout_chattingList.addView(newView)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        if(!call.isCanceled)
            call.cancel()
    }
}
