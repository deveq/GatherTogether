package com.soldemom.navermapactivity.fragment

import android.view.View
import androidx.viewpager.widget.PagerAdapter

class TestPagerAdapter : PagerAdapter() {

    override fun getCount(): Int {
        return 4
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }


}