package com.soldemom.navermapactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() , OnMapReadyCallback {

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var mapFragment: MapFragment
    lateinit var point: Point
    lateinit var latLng: LatLng
    lateinit var tag: String
    var memberBelongs : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        cancel_btn
        attend_btn
        member_count
        detail_text
        detail_title

        val uid = auth.currentUser!!.uid


        attend_btn.setOnClickListener {

            val message = if (memberBelongs) "모임에서 나가시겠습니까?" else "참여하시겠습니까?"

            AlertDialog.Builder(this)
                .setMessage(message)
                .setNegativeButton("취소",null)
                .setPositiveButton("확인") { _, _ ->

                    val list = point.member
                    list!!.add(uid)

                    val tagRef = db.collection("markers")
                        .document(tag)
                    
                    if (memberBelongs) {
                        tagRef.update(
                            "member",
                            FieldValue.arrayRemove(uid),
                            "currentCount",
                            FieldValue.increment(-1))
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "실패용...ㅠㅠ",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    } else {
                        tagRef.update(
                            "member",
                            FieldValue.arrayUnion(uid),
                            "currentCount",
                            FieldValue.increment(1))
                    //update를 통해 member라는 배열에 uid 추가.
                    //될지 모르겠지만... currentCount를 동시에 1 증가시키기.
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "성공해따 ㅠㅠ",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }.create().show()

        }

        cancel_btn.setOnClickListener {
            finish()
        }




        // Firestore의 document의 id를 tag에 넣고 intent로 받았음.
        tag = intent.getStringExtra("tag")

        //해당 id의 도큐먼트를 찾아서 get 한 후
        db.collection("markers").document(tag)
            .get()
            .addOnSuccessListener {

                //document를 Point객체로 받음.
                point = it.toObject(Point::class.java)!!

                detail_text.text = point.text
                detail_title.text = point.title
                member_count.text = "${point.currentCount} / ${point.maxCount}"
                val geoPoint = point.geoPoint!!

                latLng = LatLng(geoPoint.latitude, geoPoint.longitude)

                if (point.member!!.contains(uid)) {
                    memberBelongs = true
                    attend_btn.text = "탈퇴하기"
                }

                mapFragment.getMapAsync(this)
            }

        mapFragment = supportFragmentManager.findFragmentById(R.id.detail_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.detail_map, it).commit()
            }


    }

    override fun onMapReady(naverMap: NaverMap) {

        val cameraUpdate = CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Fly)
        naverMap.moveCamera(cameraUpdate)

        Marker().apply {
            position = LatLng(point.geoPoint!!.latitude, point.geoPoint!!.longitude)
            map = naverMap
            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this@DetailActivity) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return point.title!!
                }
            }
            infoWindow.open(this)
        }

    }


}