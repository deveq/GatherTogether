package com.soldemom.studytogether.detail.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detail_memeber_item_list.view.*

class DetailMemberListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val memberName = itemView.member_name
    val memberLeader = itemView.member_leader
    val memberImage = itemView.member_image
    val memberIntroduce = itemView.member_introduce



}