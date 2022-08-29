package com.example.myapplication.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.network.MyWebSocketClient
import com.example.myapplication.network.bean.MessageBean
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


@OptIn(DelicateCoroutinesApi::class)
class WebSocketClientService : Service() {
    lateinit var webSocketClient:MyWebSocketClient
    val webSocketUrl: URI = URI.create("ws://110.40.156.9:9090/websocket")
    private val mBinder = WebSocketClientBinder()
    private lateinit var sp:SharedPreferences
    private lateinit var token:String


    inner class WebSocketClientBinder:Binder(){
        fun getWebSocketService():WebSocketClientService{
            return this@WebSocketClientService
        }
    }

    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences("TOKEN",Context.MODE_PRIVATE)
        token = sp.getString("TOKEN","SB")!!

        GlobalScope.launch (Dispatchers.IO){
            webSocketClient = object : MyWebSocketClient(webSocketUrl) {
                override fun onMessage(p0: String?) {
                    super.onMessage(p0)
                    val messageBean = with(Gson()){
                        fromJson(p0,MessageBean::class.java)
                    }

                    val intent = Intent().apply {
                        action = "com.example.myapplication.msg"
                        putExtra("message",p0)
                    }
                    sendBroadcast(intent)
                    sendNotification(messageBean.from!!,messageBean.message!!)

                }

                override fun onClose(p0: Int , p1: String? , p2: Boolean) {
                    super.onClose(p0 , p1 , p2)
                    val intent = Intent().apply {
                        action = "com.example.myapplication.msg"
                        putExtra("netState",0)
                    }
                    sendBroadcast(intent)
                }

                override fun onOpen(p0: ServerHandshake?) {
                    super.onOpen(p0)
                    val intent = Intent().apply {
                        action = "com.example.myapplication.msg"
                        putExtra("netState",2)
                    }
                    sendBroadcast(intent)
                }
            }
           webSocketClient.addHeader("TOKEN",token)
           webSocketClient.connectBlocking()
        }


        val handler = Handler(mainLooper)
        val runnable = object :Runnable{
            override fun run() {
                if(webSocketClient.isClosed){
                    Thread{
                        webSocketClient.reconnectBlocking()
                    }.start()
                }
                handler.postDelayed(this,5000)
            }
        }
        handler.postDelayed(runnable,5000)
    }


    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }



    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(from:String , message:String){
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel("push","MessagePush",NotificationManager.IMPORTANCE_HIGH).apply {
                lockscreenVisibility = VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this,"push")
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(from)
            .setContentText(message)
            .setChannelId("push")
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(1,notification)
    }


}