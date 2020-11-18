package com.soldemom.navermapactivity.testFrag

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.soldemom.navermapactivity.DetailActivity
import com.soldemom.navermapactivity.Point
import com.soldemom.navermapactivity.R
import kotlinx.android.synthetic.main.new_study_dialog_layout.view.*


class TestMapFrag : Fragment(), OnMapReadyCallback {
    lateinit var locationSource: FusedLocationSource

    var markerList: MutableList<Point> = mutableListOf<Point>()
    var map: NaverMap? = null

    private lateinit var mapView: MapView
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    val intArr = listOf<Int>(2,3,4,5,6,7,8)

    lateinit var dialogView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ...
        val view = inflater.inflate(R.layout.fragment_test_map, container, false)

        dialogView = inflater.inflate(R.layout.new_study_dialog_layout, container, false)
//        dialogView.dialog_member_count_input.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().toInt() !in 2..6) {
//                    dialogView.dialog_member_count_input_layout.isErrorEnabled = true
//                    dialogView.dialog_member_count_input_layout.error = "2에서 8사이 숫자를 입력해주세요"
//
//                    dialogView.dialog_member_count_input.setText("2")
//
//                } else {
//                    dialogView.dialog_member_count_input_layout.isErrorEnabled = false
//                }
//            }
//        })

        dialogView.spinner2
        val spinnerAdapter = ArrayAdapter<Int>(requireContext(),
        android.R.layout.simple_spinner_item,
            intArr
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogView.spinner2.adapter = spinnerAdapter

         return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.testMapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


    }


    override fun onMapReady(naverMap: NaverMap) {
/*        if (map == null) {
            map = naverMap


            if (!locationSource.isActivated) {
                naverMap.locationSource = locationSource
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }


        }*/

        naverMap.setOnMapLongClickListener { _, latLng ->

            if (dialogView.getParent() != null) {
                (dialogView.getParent() as ViewGroup).removeView(
                    dialogView
                )
            }

            dialogView.apply{
                dialog_title_input.setText("")
                dialog_content_input.setText("")
            }

            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { a, b ->
                    val title = dialogView.dialog_title_input.text.toString()
                    val content = dialogView.dialog_content_input.text.toString()
//                    val maxCount = dialogView.dialog_member_count_input.text.toString().toLong()
                    val maxCount = intArr[dialogView.spinner2.selectedItemPosition]

                    val geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
                    val uid = auth.currentUser!!.uid



                    val markerRef2 = db.collection("markers").document()
                    val userRef = db.collection("users").document(uid)

                    val point = Point(geoPoint, title).apply {
                        text = content
                        currentCount = 1
                        this.maxCount = maxCount.toLong()
                        leader = uid
                        member = mutableListOf<String>(uid)
                        studyId = markerRef2.id
                    }

                    //트랜잭션으로 일괄업로드
                    db.runBatch { batch ->
                        val markerId: String = markerRef2.id
//                        markerRef2.set(point).addOnSuccessListener {
////                            Marker().apply {
////                                position = LatLng(latLng.latitude, latLng.longitude)
////                                map = naverMap
////
////                            }
////                            markerList.add(point)
//
//
//                        }
//
////                        userRef.update("studyList", FieldValue.arrayUnion(aabbaa))

                        batch.set(markerRef2,point)
                        batch.update(userRef,"studyList",FieldValue.arrayUnion(markerId))

                    }.addOnSuccessListener {
                        Toast.makeText(requireContext(), "다성공~", Toast.LENGTH_SHORT).show()
                    }
                }.create()

            builder.show()

        }

        //db가 변경되면 자동으로 업데이트 해줌.
        //기존에 트랜잭션만 사용한 후 맵에 marker만 추가했을 경우 클릭이벤트처리가 되지 않던 문제 해결
        db.collection("markers").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TestMapFrag","snapshot Error",e)
                return@addSnapshotListener
            }

            markerList = snapshot?.toObjects(Point::class.java) ?: mutableListOf()
            for (marker in markerList) {
                Marker().apply {
                    val latitude = marker.geoPoint!!.latitude
                    val longitude = marker.geoPoint!!.longitude
                    val latLng = LatLng(latitude, longitude)
                    position = latLng
                    map = naverMap
                    setOnClickListener {
                        Toast.makeText(requireContext(),"마커 클릭됨",Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("studyId", marker.studyId)
                        startActivity(intent)
                        false
                    }
                }
            }


        }






    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        //다른 프래그먼트로 넘어갔다가 돌아오면 다시 맵에 마커 표시
        mapView.getMapAsync(this)

    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}