package com.example.myapplication.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyViewModel
import com.example.myapplication.databinding.ContactListItemBinding
@RequiresApi(Build.VERSION_CODES.O)
class ContactAdapter(private val list: List<String>):RecyclerView.Adapter<ContactAdapter.ContactAdapterHolder>() {

    interface OnItemClickListener{
        fun onItemClick(view:View,position: Int)
    }

    private var mOnItemClickListener:OnItemClickListener?=null


    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener
    }


    inner class ContactAdapterHolder(private val mBinding:ContactListItemBinding):RecyclerView.ViewHolder(mBinding.root){
        fun bind(message:String,position: Int){
            mBinding.peopleContact.text = message
            mBinding.peopleContact.setOnClickListener {
                mOnItemClickListener?.onItemClick(mBinding.peopleContact, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapterHolder {
        val mBinding = ContactListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ContactAdapterHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ContactAdapterHolder, position: Int) {
        holder.bind(list[position],position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}