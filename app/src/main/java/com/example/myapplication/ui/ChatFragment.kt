package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MainActivity
import com.example.myapplication.MyViewModel
import com.example.myapplication.R
import com.example.myapplication.adapter.MessageAdapter
import com.example.myapplication.databinding.FragmentChatBinding
import com.example.myapplication.room.Message
import com.example.myapplication.spread.KeyboardChangeListener
import com.example.myapplication.spread.addLog
import com.google.android.material.bottomnavigation.BottomNavigationView

@RequiresApi(Build.VERSION_CODES.O)
class ChatFragment : Fragment() {
    private lateinit var mBinding:FragmentChatBinding
    private val mModel:MyViewModel by activityViewModels()
    private lateinit var mMainActivity:MainActivity
    private lateinit var sendEmail:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendEmail = arguments?.getString("sendEmail","1425636954@qq.com").toString()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mMainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View{
        mBinding = FragmentChatBinding.inflate(layoutInflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        initView()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initView(){
        activity?.apply {
            mModel.messageInfo.observe(this){
                mModel.messageList.clear()
                mModel.messageList.addAll(it)
                mBinding.messageRecyclerView.adapter?.notifyDataSetChanged()
                mBinding.messageRecyclerView.smoothScrollToPosition(mModel.messageList.size)
            }

            mBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this)
            mBinding.messageRecyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
            mBinding.messageRecyclerView.adapter = MessageAdapter(mModel.messageList)

            mModel.getMessage(sendEmail)
            sendEmail.addLog()

            //软键盘监听
            val keyboardHelper = KeyboardChangeListener(this).apply {
                setKeyBoardListener { _ , _ ->
                mBinding.messageRecyclerView.smoothScrollToPosition(
                    mModel.messageList.size
                )
                }
            }
        }

        mBinding.sendButton.setOnClickListener {
            val message = mBinding.editMessage.text.toString()
            sendMessage(message)
            mBinding.editMessage.setText("")
        }



    }




    private fun sendMessage(message: String){
        mMainActivity.sendMessage(message,sendEmail)
    }

}