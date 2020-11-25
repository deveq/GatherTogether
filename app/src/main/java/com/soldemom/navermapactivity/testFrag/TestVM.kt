package com.soldemom.navermapactivity.testFrag

import androidx.lifecycle.ViewModel
import com.naver.maps.map.overlay.Marker
import com.soldemom.navermapactivity.Point

class TestVM : ViewModel() {
//    lateinit var markerList: List<Point>
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    companion object {
        private var instance : TestVM? = null
        fun getInstance() =
            instance ?: synchronized(TestVM::class.java){
                instance ?: TestVM().also { instance = it }
            }
    }

}