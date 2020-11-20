package com.soldemom.navermapactivity.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soldemom.navermapactivity.ApproveUser
import com.soldemom.navermapactivity.R
import com.soldemom.navermapactivity.User

class ApproveAdapter(
    val approveLambda: (user: ApproveUser) -> Unit,
    val denyLambda: (user: ApproveUser) -> Unit
) : RecyclerView.Adapter<ApproveViewHolder>() {

    var list = listOf<ApproveUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApproveViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.member_approve_list_item, parent, false)
        return ApproveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApproveViewHolder, position: Int) {
        val approveUser = list[position]
        holder.approveIntroduce.text = approveUser.introduce
        holder.approveName.text = approveUser.name
        holder.approveButton.setOnClickListener {
            approveLambda(approveUser)
        }
        holder.denyButton.setOnClickListener {
            denyLambda(approveUser)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}