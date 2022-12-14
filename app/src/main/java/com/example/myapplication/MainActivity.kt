package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.*
import android.view.View
import android.widget.LinearLayout.VERTICAL
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.MessageAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.network.MyWebSocketClient
import com.example.myapplication.network.bean.MessageBean
import com.example.myapplication.room.Message
import com.example.myapplication.room.MessageRepository
import com.example.myapplication.room.UserDatabase
import com.example.myapplication.service.WebSocketClientService
import com.example.myapplication.spread.KeyboardChangeListener
import com.example.myapplication.spread.addLog
import com.example.myapplication.spread.showToast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityMainBinding
    private val mModel:MyViewModel by viewModels()
    private val database by lazy { UserDatabase.getDataBase(this) }
    private val messageRepository by lazy { MessageRepository(database.MessageDao()) }
    private lateinit var sp:SharedPreferences
    lateinit var token:String
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
            "service??????".addLog()
        }
    }





    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginActivity.context.finish()
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        //??????websocket??????
        val bindIntent = Intent(this,WebSocketClientService::class.java)
        bindService(bindIntent,serviceConnection, BIND_AUTO_CREATE)
        sp = getSharedPreferences("TOKEN",Context.MODE_PRIVATE)
        token = sp.getString("TOKEN","SB")!!
//
        val naviHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as  NavHostFragment
        val naviController = naviHostFragment.findNavController()
//        NavigationUI.setupWithNavController(mBinding.naviMain,naviController)

//        val naviController = findNavController(R.id.fragmentContainerView)
        mBinding.naviMain.setupWithNavController(naviController)

        //??????????????????
        val receiver = ChatMessageReceiver()
        val filter = IntentFilter("com.example.myapplication.msg")
        registerReceiver(receiver,filter)


        //?????????????????????
        val keyboardHelper = KeyboardChangeListener(this).apply {
            setKeyBoardListener { _ , _ ->
//                mBinding.chatView.smoothScrollToPosition(
//                    mModel.messageList.size
//                )
            }
        }


        //???????????????????????????????????????
        mModel.message.observe(this){
            val messageBean = with(Gson()){
                this.fromJson(it,MessageBean::class.java)
            }
            lifecycleScope.launch(Dispatchers.IO){
                val time = LocalDateTime.now()
                messageRepository.insertMessage(Message(messageBean.from!!,messageBean.to!!,messageBean.message!!,time))
            }
        }

        //????????????????????????
        mModel.messageInfo.observe(this){
            mModel.messageList.clear()
            mModel.messageList.addAll(it)
//            mBinding.chatView.apply {
//                adapter?.notifyDataSetChanged()
//                smoothScrollToPosition(mModel.messageList.size)
//            }

        }
        //??????????????????
        mModel.netState.observe(this){
            when(it){
                0->{
                    "??????????????????????????????".showToast()
                }
                2->{
                    "????????????".showToast()
                }
                else->{

                }
            }
        }


//        //???????????????
//        mBinding.apply {
//            switchEmail.setOnClickListener {
//                val email =toEmail.text.toString()
//                sp.edit().apply{
//                    putString("sendEmail",email)
//                    apply()
//                    "????????????".showToast()
//                }
//            }
//            sendButton.setOnClickListener {
//                val message = sendMessage.text.toString()
//                val sendEmail = sp.getString("sendEmail",token)!!
//                sendMessage(message,sendEmail)
//                sendMessage.setText("")
//            }
//
//            chatView.layoutManager = LinearLayoutManager(this@MainActivity)
//            chatView.addItemDecoration(DividerItemDecoration(this@MainActivity,VERTICAL))
//            chatView.adapter = MessageAdapter(mModel.messageList)
//        }
        //????????????
        checkNotification(this)
    }
    fun sendMessage(message:String,sendEmail:String){
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
            "??????????????????".showToast()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        mBinding.naviMain.visibility = View.VISIBLE
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
            AlertDialog.Builder(context).setTitle("????????????")
                .setMessage("???????????????????????????????????????????????????????????????????????????")
                .setPositiveButton("??????" ,
                    DialogInterface.OnClickListener { _ , _ -> goToSetting() })
                .setNegativeButton("??????" ,
                    DialogInterface.OnClickListener { _ , _ -> }).show()
        }
    }


    private fun goToSetting() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= 26) { // android 8.0??????
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("android.provider.extra.APP_PACKAGE" , packageName)
        } else if (Build.VERSION.SDK_INT >= 21) { // android 5.0-7.0
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package" , packageName)
            intent.putExtra("app_uid" , applicationInfo.uid)
        } else { //??????
            intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            intent.data = Uri.fromParts("package" , packageName , null)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }







}