package com.soldemom.studytogether.detail.recycler

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soldemom.studytogether.R
import com.soldemom.studytogether.main.dto.User

class DetailMemberListAdapter(
    val activity: Activity
) : RecyclerView.Adapter<DetailMemberListViewHolder>() {

    var memberList = listOf<User>()
    var leader: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailMemberListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.detail_memeber_item_list,parent,false)
        return DetailMemberListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailMemberListViewHolder, position: Int) {
        val user = memberList[position]
        holder.memberName.text = user.name

        if (leader == user.uid) {
            holder.memberLeader.apply {
                text = leader
                visibility = View.VISIBLE
            }
        }

        if (user.image != null) {
            Glide
                .with(activity)
                .load(user.image)
                .into(holder.memberImage)
        } else {
            holder.memberImage.setImageDrawable(activity.getDrawable(R.drawable.ic_android_black_24dp))
        }

        holder.memberIntroduce.text = user.introduce


    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}