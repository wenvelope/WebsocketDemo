package com.example.myapplication.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.MessageItemBinding
import com.example.myapplication.network.bean.MessageBean
import com.example.myapplication.room.Message
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)

class MessageAdapter(private val list:List<Message>):RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val mBinding: MessageItemBinding):RecyclerView.ViewHolder(mBinding.root){
            fun bind(itemBean:Message){
                mBinding.message.text = itemBean.message
                mBinding.email.text = itemBean.from
                mBinding.time.text = itemBean.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
    }



    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ItemViewHolder {
        val mBinding = MessageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder , position: Int) {
        val itemBean = list[position]
        holder.bind(itemBean)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}