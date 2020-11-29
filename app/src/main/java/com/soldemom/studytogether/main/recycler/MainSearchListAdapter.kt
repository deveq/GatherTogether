package com.soldemom.studytogether.main.recycler

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soldemom.studytogether.R
import com.soldemom.studytogether.main.dto.Point

class MainSearchListAdapter(
    val searchFragToDetailActivity: (studyId: String) -> Unit,
    val activity: Activity
) : RecyclerView.Adapter<MainSearchListViewHolder>() {
    lateinit var studyList: MutableList<Point>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSearchListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.search_list_item, parent, false)

        return MainSearchListViewHolder(view)

    }

    override fun onBindViewHolder(holder: MainSearchListViewHolder, position: Int) {
        val point = studyList[position]
        holder.searchTitle.text = point.title
        holder.searchText.text = point.text
        holder.searchLocation.text = point.address
        holder.itemView.setOnClickListener {
            searchFragToDetailActivity(point.studyId!!)
        }
        if (point.image != null) {
            Glide
                .with(activity)
                .load(point.image)
                .into(holder.searchImage)
        } else {
            holder.searchImage.setImageDrawable(activity.getDrawable(R.drawable.ic_android_black_24dp))
        }

    }

    override fun getItemCount(): Int {
        return studyList.size
    }
}