package com.capston.recipe.Utils

import android.util.Log
import androidx.fragment.app.Fragment
import com.capston.recipe.R
import kotlinx.android.synthetic.main.fragment_chatting.view.*
import kotlinx.android.synthetic.main.layout_chatting_opponent.view.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import retrofit2.Response
import java.net.URI


class WebSocketClientChatting(val fragment: Fragment, uri: URI) : WebSocketClient(uri) {
    lateinit var listener:ResponseController
    lateinit var openListener:OnOpenMessage
    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i("tag_open", "open : ${handshakedata}")
        openListener.onOpenChatting()
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i("tag_close", "close")
    }

    override fun onMessage(message: String?) {
        Log.i("message_tag", "msg $message")
        fragment.activity!!.runOnUiThread {
            val json = JSONObject(message)
            listener.onMessageListener(json.getInt("user"), json.getString("message"), json.getBoolean("isRecipe"))
        }

    }

    override fun onError(ex: Exception?) {
        Log.i("err_tag", "err ${ex?.message}")
    }
    interface ResponseController{
        fun onMessageListener(userId:Int?, message:String?, isRecipe:Boolean)
    }
    interface OnOpenMessage{
        fun onOpenChatting()
    }
    fun setOnOpenControlListener(listener:OnOpenMessage){
        this.openListener =listener
    }
    fun setOnResponseControlListener(listener:ResponseController){
        this.listener =listener
    }
}