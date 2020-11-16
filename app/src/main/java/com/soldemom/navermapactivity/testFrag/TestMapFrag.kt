package com.soldemom.navermapactivity.testFrag

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
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
import com.soldemom.navermapactivity.Point
import com.soldemom.navermapactivity.R
import kotlinx.android.synthetic.main.new_study_dialog_layout.view.*


class TestMapFrag : Fragment(), OnMapReadyCallback {
    lateinit var locationSource: FusedLocationSource

    var markerList: List<Point> = listOf<Point>()
    var map: NaverMap? = null

    private lateinit var mapView: MapView
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()

    lateinit var dialogView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ...
        val view = inflater.inflate(R.layout.fragment_test_map, container, false)

        dialogView = inflater.inflate(R.layout.new_study_dialog_layout, container, false)

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

            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { a, b ->
                    val title = dialogView.dialog_title_input.text.toString()
                    val content = dialogView.dialog_content_input.text.toString()
                    val memberCount = dialogView.dialog_member_count_input.text.toString().toLong()
                    val geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
                    val uid = auth.currentUser!!.uid

                    val markerRef2 = db.collection("markers").document()
                    val userRef = db.collection("users").document(uid)

                    val point = Point(geoPoint, title).apply {
                        text = content
                        currentCount = memberCount
                        leader = uid
                        member = mutableListOf<String>(uid)
                        studyId = markerRef2.id
                    }

                    //트랜잭션으로 일괄업로드
                    db.runBatch { batch ->
                        val aabbaa: String = markerRef2.id
                        markerRef2.set(point).addOnSuccessListener {
                            Marker().apply {
                                position = LatLng(latLng.latitude, latLng.longitude)
                                map = naverMap
                            }
                        }

                        userRef.update("studyList", FieldValue.arrayUnion(aabbaa))

                    }.addOnSuccessListener {
                        Toast.makeText(requireContext(), "다성공~", Toast.LENGTH_SHORT).show()
                    }
                }.create()

            builder.show()

        }
        for (marker in markerList) {
            Marker().apply {
                val latitude = marker.geoPoint!!.latitude
                val longitude = marker.geoPoint!!.longitude
                val latLng = LatLng(latitude, longitude)
                position = latLng
                map = naverMap
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

        //다른 프래그먼트로 넘어갔다가 돌아오면 다시 맵 표시
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