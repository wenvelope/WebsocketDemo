package com.example.myapplication.network

import android.net.Uri
import com.example.myapplication.spread.addLog
import com.example.myapplication.spread.showToast
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

open class MyWebSocketClient(url:URI): WebSocketClient(url) {


    override fun onOpen(p0: ServerHandshake?) {
        "open".addLog()
    }

    override fun onMessage(p0: String?) {
       "message".addLog()
    }

    override fun onClose(p0: Int , p1: String? , p2: Boolean) {
        "close".addLog()
    }

    override fun onError(p0: Exception?) {
        "error".addLog()
    }
}