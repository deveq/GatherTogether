package com.soldemom.navermapactivity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class MapFragmentActivity : FragmentActivity(), OnMapReadyCallback {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

//        setContentView(R.layout.map_fragment_activity)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

    }

    @UiThread
    override fun onMapReady(p0: NaverMap) {


    }
}