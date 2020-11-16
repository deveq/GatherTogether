package com.soldemom.navermapactivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.soldemom.navermapactivity.fragment.*
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity(), OnMapReadyCallback {

/*    var locationPermission = false
    var latitude = 0.0
    var longitude = 0.0
    var latLngChecked = false*/

    val REQUEST_CODE = 1004

    lateinit var mapFragment: MapFragment
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        val adapter = FragmentAdapter(this)

        mapFragment = MapFragment()

        val fragmentList = listOf<Fragment>(
            mapFragment,
            AttendListFragment(),
            MessageFragment(),
            MyInfoFragment()
        )

        //각 탭에 해당하는 프래그먼트 생성 후 어댑터에 넣어주기
        adapter.fragmentList = fragmentList
        test_view_pager.adapter = adapter

        //ViewPager의 스와이핑으로 넘어가는 기능 없애기
        test_view_pager.isUserInputEnabled = false

        //ViewPager와 TabLayout을 이어주기
        TabLayoutMediator(test_tab_layout,test_view_pager) { _,_ ->
        }.attach()  // attach() 필수!

        mapFragment.getMapAsync(this)

/*
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            locationPermission = true
            Toast.makeText(this,"위치땃당",Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        }
*/

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)



    }

    override fun onMapReady(naverMap: NaverMap) {

        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        naverMap.locationTrackingMode = LocationTrackingMode.Follow



   /*     val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val enabledProviders = locationManager.getProviders(true)

        for (i in enabledProviders) {
            var location: Location
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(i)

                latitude = location.latitude
                longitude = location.longitude


            } else {
                break
            }


        }

        if (locationPermission) {
            val latLng: LatLng = LatLng(latitude,longitude)
            val cameraUpdate = CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Fly)
            naverMap.moveCamera(cameraUpdate)
        }*/


        naverMap.setOnMapLongClickListener { pointF, latLng ->
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("모임 만들기")
                .setMessage("모임을 만드시겠습니까?")
                .setNegativeButton("취소",null)
                .setPositiveButton("확인") { _, _ ->

                    val intent = Intent(this, CreateGatheringActivity::class.java)

                    intent.putExtra("latitude",latLng.latitude)
                    intent.putExtra("longitude",latLng.longitude)
                    startActivityForResult(intent,REQUEST_CODE)
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
                            val intent = Intent(this@TestActivity,DetailActivity::class.java)
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


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_list,menu)

        return super.onCreateOptionsMenu(menu)
    }




    fun showText(text: String) {
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mapFragment.getMapAsync(this)

        }


    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        return super.onContextItemSelected(item)
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == R.id.menu_logout) {
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
