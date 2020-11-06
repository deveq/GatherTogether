package com.soldemom.navermapactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    val REQUEST_CODE = 1004

    var auth: FirebaseAuth = Firebase.auth
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fm = supportFragmentManager
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == R.id.menu_logout) {
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    override fun onMapReady(naverMap: NaverMap) {

        naverMap.setOnMapLongClickListener { pointF, latLng ->
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("여기에 만들기")
                .setMessage("스터디 ??")
                .setNegativeButton("취소",null)
                .setPositiveButton("확인") { _, _ ->

                    val intent = Intent(this, CreateGatheringActivity::class.java)

                    intent.putExtra("latitude",latLng.latitude)
                    intent.putExtra("longitude",latLng.longitude)
                    startActivityForResult(intent,REQUEST_CODE)

//                    val latitude = latLng.latitude
//                    val longitude = latLng.longitude
//
//                    db.collection("markers").add(
//                        Point(GeoPoint(latitude, longitude), "스터디하실분")
////
////
////
////                        Marker().apply{
////                        position = latLng
//////                        map = naverMap      //이거 땜에 바로 등록될것같은데..
////                        tag = "스터디하실분"
////                    }
//            ).addOnSuccessListener {
//                        Toast.makeText(this@MainActivity, "올리기 성공", Toast.LENGTH_SHORT).show()
//                        Marker().apply{
//                            position = latLng
//                            map = naverMap
//                        }
//                    }
//                        .addOnFailureListener {
//                            Toast.makeText(this@MainActivity, "실패,,,", Toast.LENGTH_SHORT).show()
//                        }
                }
                .create()
                .show()
        }

        //여기에 DB 받고. addOnSuccess 넣고, for문 돌려서 Marker생성하고
        db.collection("markers")
            .get()
            .addOnSuccessListener {

                for (i in it) {
                    val geoPoint = i["geoPoint"] as GeoPoint
                    val text = i["title"] as String
                    val latLng = LatLng(geoPoint.latitude,geoPoint.longitude)
                    Marker().apply {
                        position = latLng
                        map = naverMap
                        tag = i.id
                        setOnClickListener {
                            val intent = Intent(this@MainActivity,DetailActivity::class.java)
                            intent.putExtra("tag",tag as String)
                            startActivity(intent)
                            false
                        }

                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"왜 실패져",Toast.LENGTH_SHORT).show()
            }





//
//        Marker().apply {
//            position = LatLng(37.5666102, 126.9783881)
//            map = naverMap
//        }
//
//        Marker().apply {
//            position = LatLng(37.57000, 126.97618)
//            icon = MarkerIcons.BLACK
//            angle = 315f
//            map = naverMap
//            captionText = "하이영"
//        }
//
//        Marker().apply {
//            position = LatLng(37.56500, 126.9783881)
//            icon = MarkerIcons.BLACK
//            iconTintColor = Color.RED
//            alpha = 0.5f
//            map = naverMap
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_list,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        return super.onContextItemSelected(item)
    }



    fun showText(text: String) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mapFragment.getMapAsync(this)

        }


    }
}