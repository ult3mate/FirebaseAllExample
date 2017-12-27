package com.beer.android.firebaseallfeatureproject.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.beer.android.firebaseallfeatureproject.R
import com.beer.android.firebaseallfeatureproject.adapter.holder.MyMessageViewHolder
import com.beer.android.firebaseallfeatureproject.adapter.holder.OtherMessageViewHolder
import com.beer.android.firebaseallfeatureproject.model.MessageData

/**
 * Created by pattadon on 12/27/17.
 */

open class MessageAdapter(private var messageDataList: List<MessageData>, private var currentUser : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_OTHER = 0
    private val TYPE_USER = 1

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_USER) {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_message_user, parent, false)
            return MyMessageViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_message_other_user, parent, false)
            return OtherMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val message = messageDataList[position].message
        if (holder is MyMessageViewHolder) {
            holder.textView?.text = message.toString()
        }else if(holder is OtherMessageViewHolder) {
            holder.textView?.text = message.toString()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val user = messageDataList[position].message.user
        return if (user.equals(currentUser, ignoreCase = true)) {
            TYPE_USER
        } else TYPE_OTHER
    }

    override fun getItemCount(): Int {
        return messageDataList.size
    }
}