package com.soldemom.navermapactivity.testFrag

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.test_item_list.view.*

class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val titleView = itemView.recycler_title
    val contentView = itemView.recycler_content
    val memberView = itemView.recycler_member

}