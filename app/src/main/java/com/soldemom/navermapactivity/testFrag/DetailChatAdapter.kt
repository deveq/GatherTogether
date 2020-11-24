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
import java.lang.RuntimeException
import java.text.SimpleDateFormat

class DetailChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var chatList : MutableList<Chat> = mutableListOf<Chat>()
    lateinit var user: User
    lateinit var currentUserUid: String
    lateinit var dateTemp: String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View
        val inflater = LayoutInflater.from(parent.context)

        // viewType에 따라서 반환해주는 ViewHolder를 다르게.
        return when (viewType) {
            Chat.CHAT_TYPE -> {
                view = inflater.inflate(R.layout.detail_chat_list_item, parent, false)
                DetailChatViewHolder(view)
            }
            Chat.DATE_TYPE -> {
                view = inflater.inflate(R.layout.detail_chat_date_list_item, parent, false)
                DetailChatDateViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val chat = chatList[position]

        when (chat.type) {
            Chat.CHAT_TYPE -> {
                holder as DetailChatViewHolder
                //만약 본인이 보낸 chat이라면,
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
                } else { //상대방이 보낸 chat이라면
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
            Chat.DATE_TYPE -> {
                // 날짜라면.
                holder as DetailChatDateViewHolder
                holder.detailChatDate.text = chat.text
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return chatList[position].type
    }
}