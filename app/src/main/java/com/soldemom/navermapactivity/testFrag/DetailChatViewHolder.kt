package com.soldemom.navermapactivity.testFrag

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detail_chat_list_item.view.*

class DetailChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val chatText = itemView.chat_text
    val chatName = itemView.chat_name
    val chatTime = itemView.chat_time
}