package com.soldemom.navermapactivity.testFrag

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TestFragAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    lateinit var list: List<Fragment>

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}