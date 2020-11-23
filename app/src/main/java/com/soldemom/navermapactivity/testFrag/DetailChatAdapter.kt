package com.soldemom.navermapactivity.testFrag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.soldemom.navermapactivity.Chat
import com.soldemom.navermapactivity.R
import com.soldemom.navermapactivity.User
import kotlinx.android.synthetic.main.detail_chat_list_item.view.*
import java.text.SimpleDateFormat

class DetailChatAdapter : RecyclerView.Adapter<DetailChatViewHolder>() {
    var chatList : MutableList<Chat> = mutableListOf<Chat>()
    lateinit var user: User
    lateinit var currentUserUid: String
    lateinit var dateTemp: String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.detail_chat_list_item, parent, false)

        return DetailChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailChatViewHolder, position: Int) {
        val chat = chatList[position]
        if (chat.uid == user.uid) {
            holder.apply {
                chatText.setBackgroundResource(R.drawable.chat_right)

                val textLayoutParam = chatText.layoutParams as ConstraintLayout.LayoutParams
                textLayoutParam.apply {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    leftToLeft = ConstraintLayout.LayoutParams.UNSET
                }

                val timeLayoutParam = (chatTime.layoutParams as ConstraintLayout.LayoutParams)
                timeLayoutParam.apply {
                    rightToLeft = R.id.chat_text
                    leftToRight = ConstraintLayout.LayoutParams.UNSET
                }

                chatName.visibility = View.GONE
            }
        } else {
            holder.apply {
                chatText.setBackgroundResource(R.drawable.chat_left)


                val textLayoutParam = chatText.layoutParams as ConstraintLayout.LayoutParams
                textLayoutParam.apply {
                    topToBottom = R.id.chat_name
                    topToTop = ConstraintLayout.LayoutParams.UNSET
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToRight = ConstraintLayout.LayoutParams.UNSET
                }

                val timeLayoutParam = (chatTime.layoutParams as ConstraintLayout.LayoutParams)
                timeLayoutParam.apply {
                    rightToLeft = ConstraintLayout.LayoutParams.UNSET
                    leftToRight = R.id.chat_text

                }

                chatName.visibility = View.VISIBLE
            }

        }

        holder.chatName.text = user.name
        holder.chatText.text = chat.text

        val sdf = SimpleDateFormat("HH:mm")
        holder.chatTime.text = sdf.format(chat.time)


    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}