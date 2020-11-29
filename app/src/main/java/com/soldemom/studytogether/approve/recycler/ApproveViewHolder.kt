package com.soldemom.studytogether.approve.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.member_approve_list_item.view.*

class ApproveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val approveName = itemView.approve_list_name
    val approveIntroduce = itemView.approve_list_introduce
    val approveButton = itemView.approve_button
    val denyButton = itemView.deny_button

}