package com.soldemom.navermapactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.soldemom.navermapactivity.testFrag.DetailAdapter
import com.soldemom.navermapactivity.testFrag.DetailChatFragment
import com.soldemom.navermapactivity.testFrag.DetailFragAdapter
import com.soldemom.navermapactivity.testFrag.StudyInfoFragment
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var point: Point
    lateinit var uid: String
    lateinit var studyId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        studyId = intent.getStringExtra("studyId")

        val list = mutableListOf<Fragment>(
            StudyInfoFragment(studyId),
            DetailChatFragment(studyId)
        )

        val adapter = DetailFragAdapter(this)
        adapter.list = list
        detail_tab_layout
        detail_view_pager.adapter = adapter

        TabLayoutMediator(detail_tab_layout,detail_view_pager) { tab, position ->
            tab.text = when (position) {
                1 -> "대화방"
                else -> "스터디정보"
            }
        }.attach()
    }
}
