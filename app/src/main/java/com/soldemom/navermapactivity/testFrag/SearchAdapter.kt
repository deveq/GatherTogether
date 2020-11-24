package com.soldemom.navermapactivity.testFrag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soldemom.navermapactivity.DetailActivity
import com.soldemom.navermapactivity.Point
import com.soldemom.navermapactivity.R

class SearchAdapter(
    val searchFragToDetailActivity: (studyId: String) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {
    lateinit var studyList: MutableList<Point>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.search_list_item, parent, false)

        return SearchViewHolder(view)

    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val point = studyList[position]
        holder.searchTitle.text = point.title
        holder.searchText.text = point.text
        holder.searchLocation.text = point.address
        holder.itemView.setOnClickListener {
            searchFragToDetailActivity(point.studyId!!)
        }

    }

    override fun getItemCount(): Int {
        return studyList.size
    }
}