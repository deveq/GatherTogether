package com.soldemom.navermapactivity.testFrag

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soldemom.navermapactivity.Point
import com.soldemom.navermapactivity.R

class TestAdapter(
    val itemLambda: (point: Point) -> Unit
) : RecyclerView.Adapter<TestViewHolder>() {

    var studyList: MutableList<Point> = mutableListOf<Point>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.test_item_list,parent,false)

        return TestViewHolder(view)

    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val point = studyList[position]

        holder.contentView.text = point.text
        holder.titleView.text = point.title
        holder.memberView.text = "${point.currentCount}/${point.maxCount}"
        holder.itemView.setOnClickListener {
            itemLambda(point)
        }


    }

    override fun getItemCount(): Int {
        return studyList.size
    }
}