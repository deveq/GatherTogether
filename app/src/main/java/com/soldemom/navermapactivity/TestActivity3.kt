package com.soldemom.navermapactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.soldemom.navermapactivity.fragment.MapFrag
import com.soldemom.navermapactivity.testFrag.*
import kotlinx.android.synthetic.main.activity_test3.*

class TestActivity3 : AppCompatActivity() {

    val tag = "FCM"
    private lateinit var locationSource: FusedLocationSource

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var markerList: MutableList<Point>
    lateinit var viewModel: TestVM
    val mapFrag = TestMapFrag()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test3)


        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mapFrag.locationSource = locationSource

        getMarkers()

//        FirebaseMessaging.getInstance().token.addOnCompleteListener {task ->
//            if (!task.isSuccessful) {
//                Log.w(tag, "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//            }
//
//            // 새로운 FCM registration token을 얻음
//            val token = task.result
//            db.collection("tokens").add("token" to token!!)
//
//            //토큰 확인
//            Log.d(tag, token)
//        }




    }

    private fun getMarkers() {
        db.collection("markers")
            .get()
            .addOnSuccessListener { querySnapshot ->
                markerList = querySnapshot.toObjects(Point::class.java)
                mapFrag.markerList = markerList

                //

                val fragList = listOf<Fragment>(
                    mapFrag,
                    TestAttendFrag(),
                    TestInfoFrag()
                )

                testViewPager.adapter = TestFragAdapter(this).apply {
                    list = fragList
                }

                testViewPager.isUserInputEnabled = false
                
                val nameList = listOf<String>("찾기","가입목록","정보")

                TabLayoutMediator(testTab, testViewPager) { tab, position ->
                    tab.text = nameList[position]
                }.attach()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_list,menu)

        return super.onCreateOptionsMenu(menu)
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


}