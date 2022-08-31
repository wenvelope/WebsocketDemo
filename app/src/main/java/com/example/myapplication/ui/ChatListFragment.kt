package com.example.myapplication.ui

import android.os.Binder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MyViewModel
import com.example.myapplication.R
import com.example.myapplication.adapter.ContactAdapter
import com.example.myapplication.databinding.FragmentChatListBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

@RequiresApi(Build.VERSION_CODES.O)
class ChatListFragment : Fragment() {

    private lateinit var mBinding:FragmentChatListBinding
    private val mModel:MyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentChatListBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView(){
        val adapter = ContactAdapter(mModel.chatList)
        adapter.setOnItemClickListener(object :ContactAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
              activity?.apply {
                  val naviMain = findViewById<BottomNavigationView>(R.id.navi_main)
                  naviMain.visibility = View.GONE
                  val argument = Bundle().apply {
                      putString("sendEmail",mModel.chatList[position])
                  }
                  findNavController().navigate(R.id.action_chatListFragment_to_chatFragment,argument)
              }
            }
        })

        mBinding.chatRecyclerView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        mBinding.chatRecyclerView.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
        mBinding.chatRecyclerView.adapter = adapter

    }


}