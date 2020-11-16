package com.soldemom.navermapactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.naver.maps.map.*

class TestActivity2 : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapmap) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapmap, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {






    }
}