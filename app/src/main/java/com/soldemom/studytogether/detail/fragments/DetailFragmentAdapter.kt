package com.soldemom.studytogether.detail.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class DetailFragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    lateinit var list : MutableList<Fragment>

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}