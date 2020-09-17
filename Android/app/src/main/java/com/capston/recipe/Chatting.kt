package com.capston.recipe


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capston.recipe.Api.chatApi.ChatService
import com.capston.recipe.Api.recipeAndStoryApi.RecipeService
import com.capston.recipe.Items.ChattingApiItem
import com.capston.recipe.Items.ChattingListApiItem
import com.capston.recipe.Items.RecipeDetailApiItem
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.*
import kotlinx.android.synthetic.main.fragment_chatting.view.*
import kotlinx.android.synthetic.main.fragment_ta_recipe_detail.view.*
import kotlinx.android.synthetic.main.layout_chatting_mine.view.*
import kotlinx.android.synthetic.main.layout_chatting_opponent.view.*
import org.java_websocket.client.WebSocketClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI

class Chatting : Fragment() {
    val TAG="Chatting"
    private lateinit var mSocket:WebSocketClient
    lateinit var sft:SharedPreferenceTool
    var user:UserApiItem? = null
    var opponentUserId = -1
    var recipeId = -1
    var clientChatting:WebSocketClientChatting? = null
    var clientNotice: WebSocketClientWaiting? = null
    lateinit var uri:URI
    lateinit var fragmentInflater: View
    lateinit var inflater:LayoutInflater
    var container:ViewGroup? = null
    var tempRecipeCheck:Boolean=false
    // 기존 채팅이 있으면 해당 채팅방에 레시피 하나만 올려놓기
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup? ,savedInstanceState: Bundle?): View? {
        sft = SharedPreferenceTool(context!!)
        val tempUser = sft.loadObjectSharedPreference<UserApiItem>(USER)
        if(tempUser!=null){
            user = tempUser
        }
        opponentUserId = arguments!!.getInt("opponentUserId")
        recipeId = arguments!!.getInt("recipeId")
        if(recipeId>0)
            tempRecipeCheck=true
        if(opponentUserId==-1){
            Toast.makeText(context!!, "초기화 오류.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        else {
            this.inflater = inflater
            this.container = container
            fragmentInflater = inflater.inflate(R.layout.fragment_chatting, container, false)
            fragmentInflater.layout_chatting_forTouch.setOnClickListener {
                Toast.makeText(context!!, "asddddd", Toast.LENGTH_SHORT).show()
                hideKeyboard()
            }
            getChattingBefore(inflater, container)
        }
        return fragmentInflater
    }
    private fun hideKeyboard(){
        val inputMethodManager= activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(fragmentInflater.windowToken, 0)
    }

    private fun getChattingBefore(inflater:LayoutInflater, container: ViewGroup?) {
        val queryMap = hashMapOf<String, Int>()
        queryMap["sender"] = user!!.id!!
        queryMap["receiver"] = opponentUserId
        val service = ChatService(ServerAddress.serverAddress).service
        val call = service.getLastChatting(queryMap)
        call.enqueue(object : Callback<ChattingApiItem?>{
            override fun onResponse(
                call: Call<ChattingApiItem?>,
                response: Response<ChattingApiItem?>
            ) {
                val responseBody = response.body()
                if(responseBody!=null){
                    (activity as AppCompatActivity).supportActionBar?.title = responseBody!!.user.nickname
                    if(responseBody.msgList!=null)
                        for(chat in responseBody.msgList){
                            val isMine = chat.user!!.id==user!!.id
                            val type = chat.type ==1
                            if(recipeId<0)
                                if(chat.type==1)
                                    recipeId=chat.message!!.toInt()
                            displayMsg(chat.message, isMine, type)
                        }
                }
                start()
            }

            override fun onFailure(call: Call<ChattingApiItem?>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(context!!, "연결 오류.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

        })
    }

    fun getSimpleRecipeDetail(){
        //MyPageRecipeApiItem 이거 받아오기
    }
    fun displayMsg(message:String?, isMine:Boolean, isRecipe:Boolean){
        if(message!=null) {
            var newMessage:View
            if(isRecipe){ // recipe면 위에
                // recipe 받기
                val service = RecipeService(activity!!, context!!, ServerAddress.serverAddress)
                val recipeCall = service.service.getRecipeItem(recipeId)
                recipeCall!!.enqueue(object : Callback<RecipeDetailApiItem> {
                    override fun onResponse(
                        call: Call<RecipeDetailApiItem>,
                        response: Response<RecipeDetailApiItem>
                    ) {
                        val responseItem = response.body()
                        if (responseItem != null) {
                            fragmentInflater.layout_fragmentChatting_recipe.visibility=View.VISIBLE
                            if(responseItem.multiImageResult != null)
                                ImageUtils.putImageIntoView(context!!, responseItem.multiImageResult!![0].image, fragmentInflater.imageView_fragmentChatting_recipe)
                            fragmentInflater.textView_fragmentChatting_recipe.text = "질문할 레시피 : "+responseItem.recipeTitle
                            fragmentInflater.layout_fragmentChatting_recipe.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putInt("recipeId", responseItem.id!!)
                                bundle.putInt("type", ConstCodes.RECIPE)
                                findNavController().navigate(R.id.action_chatting_to_recipeDetail, bundle)
                            }
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
                    }

                })
            }else{
                if(isMine){       //나면
                    newMessage = inflater.inflate(R.layout.layout_chatting_mine, container, false)
                    newMessage.textView_chattingMine_Item.text = message
                }else{
                    newMessage = inflater.inflate(R.layout.layout_chatting_opponent, container, false)
                    newMessage.textView_chattingOpponent_Item.text = message
                }
                newMessage.isFocusableInTouchMode = true
                newMessage.requestFocus()
                fragmentInflater.layout_chatting.addView(newMessage)
            }
//                    fragmentInflater.layout_recipeDetail_commentWrapper.isFocusableInTouchMode = true
//                    fragmentInflater.layout_recipeDetail_commentWrapper.requestFocus()
//            noticeTo()
        }
    }
    fun noticeTo(url :String){
        uri = URI(url)
        clientNotice = WebSocketClientWaiting(activity!!, uri)
        clientNotice!!.connect()
        clientNotice!!.setOnResponseControlListener(object :WebSocketClientWaiting.ResponseController{
            override fun newChattingListener(flag: JSONObject) {
            }

            override fun channelOpenListener() {
                val json = JSONObject()
                json.put("opp", user!!.id)
                clientNotice!!.send(json.toString())
            }

        })
    }
    fun start() {
        val roomName=
            if(user!!.id!! >opponentUserId)
                "chat_${opponentUserId}_${user!!.id}_"
            else
                "chat_${user!!.id}_${opponentUserId}_"


        uri = URI("${ServerAddress.serverAddress}/ws/chatting/${user!!.id}/$roomName/")
        clientChatting = WebSocketClientChatting(this, uri)
        clientChatting!!.connect()
        clientChatting!!.setOnResponseControlListener(object :WebSocketClientChatting.ResponseController{
            override fun onMessageListener(userId: Int?, message: String?, isRecipe: Boolean) {
                noticeTo("${ServerAddress.serverAddress}/ws/noticeToOpponent/${user!!.id}/$opponentUserId/")
                if(user!!.id==userId){
                    displayMsg(message, true, isRecipe = isRecipe)
                }else{
                    displayMsg(message, false, isRecipe = isRecipe)
                }
            }

        })
        clientChatting!!.setOnOpenControlListener(object :WebSocketClientChatting.OnOpenMessage{
            override fun onOpenChatting() {
                if(tempRecipeCheck)
                    sendMessage(recipeId.toString(),true)
            }
        })
        fragmentInflater.button_chatting.setOnClickListener {
            val text = fragmentInflater.editText_chatting.text.toString()
            fragmentInflater.editText_chatting.setText("")
            sendMessage(text, false)
        }
    }
    fun sendMessage(message:String, isRecipe:Boolean){
        val json = JSONObject()
        json.put("user", user!!.id)
        json.put("message", message)
        json.put("isRecipe", isRecipe)
        clientChatting!!.send(json.toString())
    }

    override fun onDestroy() {
        (activity as MainActivity).checkNewMessage(opponentUserId)
        super.onDestroy()
        if(clientChatting!=null)
            clientChatting!!.close()
    }
    override fun onResume() {
        super.onResume()
        while(findNavController().currentDestination!!.id!=R.id.chatting)
            findNavController().popBackStack()
    }
}
