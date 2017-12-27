package com.beer.android.firebaseallfeatureproject.adapter.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.beer.android.firebaseallfeatureproject.R

/**
 * Created by pattadon on 12/27/17.
 */

class OtherMessageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var textView : TextView? = null

    init {
        textView = itemView?.findViewById<TextView>(R.id.adapter_other_message_tv)
    }
}