package com.capston.recipe.Utils

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import com.capston.recipe.MainActivity
import com.capston.recipe.R
import kotlinx.android.synthetic.main.fragment_chatting.view.*
import kotlinx.android.synthetic.main.layout_chatting_opponent.view.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import retrofit2.Response
import java.net.URI


class WebSocketClientWaiting(val activity: Activity, uri: URI) : WebSocketClient(uri) {
    lateinit var listener:ResponseController
    override fun connectBlocking(): Boolean {
        return super.connectBlocking()
        Log.i("tag_block", "open : ")
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        listener.channelOpenListener()
        Log.i("tag_open", "open : ${handshakedata}")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i("tag_close", "$reason")
    }

    override fun onMessage(message: String?) {
        Log.i("message_tag", "msg $message")
        activity.runOnUiThread {
            val json = JSONObject(message)
            listener.newChattingListener(json)
        }

    }

    override fun onError(ex: Exception?) {
        Log.i("err_tag", "err ${ex?.message}")
    }
    interface ResponseController{
        fun newChattingListener(json:JSONObject)
        fun channelOpenListener()
    }
    fun setOnResponseControlListener(listener:ResponseController){
        this.listener =listener
    }
}