package com.soldemom.navermapactivity.fragment

import android.util.Log
import com.naver.maps.map.MapFragment

class MapFrag : MapFragment() {


    companion object {
        private var mapFragment: MapFragment? = null
        fun getInstance() : MapFragment {
            if (mapFragment == null) {
                var mapFragment = MapFragment.newInstance()
            }
            Log.d("pathh","새로 생성됫어요!")
            return mapFragment!!
        }
    }

}