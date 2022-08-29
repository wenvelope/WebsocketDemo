package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout.VERTICAL
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.MessageAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.network.MyWebSocketClient
import com.example.myapplication.network.bean.MessageBean
import com.example.myapplication.room.Message
import com.example.myapplication.room.MessageRepository
import com.example.myapplication.room.UserDatabase
import com.example.myapplication.room.UserRepository
import com.example.myapplication.service.WebSocketClientService
import com.example.myapplication.spread.KeyboardChangeListener
import com.example.myapplication.spread.addLog
import com.example.myapplication.spread.showToast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityMainBinding
    private lateinit var mModel:MyViewModel
    private val database by lazy { UserDatabase.getDataBase(this) }
    private val messageRepository by lazy { MessageRepository(database.MessageDao()) }
    private lateinit var sp:SharedPreferences
    private lateinit var token:String
    private lateinit var mSocketClient: MyWebSocketClient
    private lateinit var mBinder:WebSocketClientService.WebSocketClientBinder
    private lateinit var mService:WebSocketClientService

    private val serviceConnection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName , service: IBinder) {
            mBinder = service  as WebSocketClientService.WebSocketClientBinder
            mService = mBinder.getWebSocketService()
            mSocketClient = mService.webSocketClient
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            "service断开".addLog()
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginActivity.context.finish()
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        //绑定websocket服务
        val bindIntent = Intent(this,WebSocketClientService::class.java)
        bindService(bindIntent,serviceConnection, BIND_AUTO_CREATE)

        mModel = ViewModelProvider(this)[MyViewModel::class.java]
        sp = getSharedPreferences("TOKEN",Context.MODE_PRIVATE)
        token = sp.getString("TOKEN","SB")!!


        //注册广播接收
        val receiver = ChatMessageReceiver()
        val filter = IntentFilter("com.example.myapplication.msg")
        registerReceiver(receiver,filter)


        //软键盘弹起监听
        val keyboardHelper = KeyboardChangeListener(this).apply {
            setKeyBoardListener { _ , _ ->
                mBinding.chatView.smoothScrollToPosition(
                    mModel.messageList.size
                )
            }
        }


        //当前发送或者接受的消息监听
        mModel.message.observe(this){
            val messageBean = with(Gson()){
                this.fromJson(it,MessageBean::class.java)
            }
            lifecycleScope.launch(Dispatchers.IO){
                val time = LocalDateTime.now()
                messageRepository.insertMessage(Message(messageBean.from!!,messageBean.to!!,messageBean.message!!,time))
            }
        }

        //聊天记录变化监听
        mModel.messageInfo.observe(this){
            mModel.messageList.clear()
            mModel.messageList.addAll(it)
            mBinding.chatView.apply {
                adapter?.notifyDataSetChanged()
                smoothScrollToPosition(mModel.messageList.size)
            }

        }
        //网络状态监听
        mModel.netState.observe(this){
            when(it){
                0->{
                    "网络连接断开正在重连".showToast()
                }
                2->{
                    "连接成功".showToast()
                }
                else->{

                }
            }
        }

        //页面初始化
        mBinding.apply {
            switchEmail.setOnClickListener {
                val email =toEmail.text.toString()
                sp.edit().apply{
                    putString("sendEmail",email)
                    apply()
                    "设置成功".showToast()
                }
            }
            sendButton.setOnClickListener {
                val message = sendMessage.text.toString()
                val sendEmail = sp.getString("sendEmail",token)!!
                sendMessage(message,sendEmail)
                sendMessage.setText("")
            }

            chatView.layoutManager = LinearLayoutManager(this@MainActivity)
            chatView.addItemDecoration(DividerItemDecoration(this@MainActivity,VERTICAL))
            chatView.adapter = MessageAdapter(mModel.messageList)
        }
        //通知权限
        checkNotification(this)
        //获取聊天记录
        mModel.getMessage()


    }

    private fun sendMessage(message:String,sendEmail:String){
        if(message.isNotEmpty()){
            val str = with(Gson()){
                this.toJson(MessageBean(token,sendEmail,message))
            }
            if(mSocketClient.isOpen){
                mSocketClient.send(str)
                if(token!=sendEmail){
                    mModel.message.postValue(str)
                }
            }
        }else{
            "发送消息为空".showToast()
        }

    }


    inner class ChatMessageReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val msg = intent.getStringExtra("message")
            if(msg!=null){
                mModel.message.postValue(msg)
            }
            val state = intent.getIntExtra("netState",1)
            mModel.netState.postValue(state)
        }
    }


    private fun checkNotification(context: Context) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            AlertDialog.Builder(context).setTitle("温馨提示")
                .setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                .setPositiveButton("确定" ,
                    DialogInterface.OnClickListener { _ , _ -> goToSetting() })
                .setNegativeButton("取消" ,
                    DialogInterface.OnClickListener { _ , _ -> }).show()
        }
    }


    private fun goToSetting() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= 26) { // android 8.0引导
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("android.provider.extra.APP_PACKAGE" , packageName)
        } else if (Build.VERSION.SDK_INT >= 21) { // android 5.0-7.0
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package" , packageName)
            intent.putExtra("app_uid" , applicationInfo.uid)
        } else { //其它
            intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            intent.data = Uri.fromParts("package" , packageName , null)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }







}