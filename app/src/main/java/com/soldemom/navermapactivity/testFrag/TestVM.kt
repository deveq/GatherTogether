package com.soldemom.navermapactivity.testFrag

import androidx.lifecycle.ViewModel
import com.naver.maps.map.overlay.Marker
import com.soldemom.navermapactivity.Point

class TestVM : ViewModel() {
    lateinit var markerList: List<Point>

}