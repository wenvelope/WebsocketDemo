package com.example.myapplication.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MyViewModel
import com.example.myapplication.R
import com.example.myapplication.adapter.ContactAdapter
import com.example.myapplication.databinding.FragmentContactBinding

@RequiresApi(Build.VERSION_CODES.O)
class ContactFragment : Fragment() {
    private lateinit var mBinding:FragmentContactBinding
    private val mModel: MyViewModel by activityViewModels()
    private val contactList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentContactBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }



    private fun initView(){
        contactList.clear()
        contactList.apply {
            add("1425636954@qq.com")
            add("1938930412@qq.com")
            add("1476001066@qq.com")
        }
        mBinding.contactList.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        mBinding.contactList.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
        val adapter = ContactAdapter(contactList)
        adapter.setOnItemClickListener(object :ContactAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                if (!mModel.chatList.contains(contactList[position])){
                    mModel.chatList.add(contactList[position])
                }
                findNavController().navigate(R.id.action_contactFragment_to_chatListFragment)
            }
        })
        mBinding.contactList.adapter = adapter
    }


}

