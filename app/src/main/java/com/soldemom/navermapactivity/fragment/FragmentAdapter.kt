package com.soldemom.navermapactivity.fragment

import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(fragmentActivity: FragmentActivity ) : FragmentStateAdapter(fragmentActivity) {
    lateinit var fragmentList: List<Fragment>
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> fragmentList[1]
            2 -> fragmentList[2]
            3 -> fragmentList[3]
            else -> fragmentList[0]
        }

    }
}