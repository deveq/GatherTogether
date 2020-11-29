package com.soldemom.studytogether.main.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_list_item.view.*

class MainSearchListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val searchImage = itemView.search_item_image
    val searchTitle = itemView.search_title
    val searchText = itemView.search_text
    val searchLocation = itemView.search_location

}