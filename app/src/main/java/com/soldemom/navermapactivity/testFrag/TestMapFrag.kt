package com.soldemom.navermapactivity.testFrag

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStoreOwner
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.soldemom.navermapactivity.*
import com.soldemom.navermapactivity.R
import com.soldemom.navermapactivity.kakaoLocal.RetrofitHelper
import com.soldemom.navermapactivity.kakaoLocal.RetrofitService
import kotlinx.android.synthetic.main.new_study_dialog_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestMapFrag : Fragment(), OnMapReadyCallback {
    lateinit var locationSource: FusedLocationSource

    var markerList: MutableList<Point> = mutableListOf<Point>()
    var map: NaverMap? = null
    lateinit var kakaoAppKey: String
    lateinit var address: String

    private lateinit var mapView: MapView
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    val intArr = listOf<Int>(2, 3, 4, 5, 6, 7, 8)

    lateinit var viewModel: TestVM

    lateinit var dialogView: View

    //    lateinit var naverMap: NaverMap
    var naverMap: NaverMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_map, container, false)

//        viewModel = ViewModelProvider(activity as ViewModelStoreOwner).get(TestVM::class.java)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TestVM::class.java)

        kakaoAppKey = getString(R.string.kakao_local_app_key)

        dialogView = inflater.inflate(R.layout.new_study_dialog_layout, container, false)

        dialogView.spinner2
        val spinnerAdapter = ArrayAdapter<Int>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            intArr
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogView.spinner2.adapter = spinnerAdapter

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.testMapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
//        this.naverMap = naverMap

        this.naverMap = naverMap

        //지도를 꾸욱 눌렀을때 스터디를 생성할 수 있는 다이얼로그 뜨도록 함
        naverMap.setOnMapLongClickListener { _, latLng ->

            val retrofit = RetrofitHelper.getRetrofit()
            val retrofitService = retrofit.create(RetrofitService::class.java)

            retrofitService
                .getByGeo2(kakaoAppKey, latLng.longitude, latLng.latitude)
                .enqueue(object: Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()!!
                            result["documents"].asJsonArray[1].asJsonObject.apply {
                                val depth1 = this["region_1depth_name"].asString
                                val depth2 = this["region_2depth_name"].asString

                                address = "$depth1 $depth2"
                            }

                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    }
                })


            if (dialogView.getParent() != null) {
                (dialogView.getParent() as ViewGroup).removeView(
                    dialogView
                )
            }

            dialogView.apply {
                dialog_title_input.setText("")
                dialog_content_input.setText("")
            }

            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { _, _ ->
                    val title = dialogView.dialog_title_input.text.toString()
                    val content = dialogView.dialog_content_input.text.toString()
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
                        address = this@TestMapFrag.address
                    }

                    //트랜잭션으로 일괄업로드
                    db.runBatch { batch ->
                        val markerId: String = markerRef2.id
                        batch.set(markerRef2, point)
                        batch.update(userRef, "studyList", FieldValue.arrayUnion(markerId))

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
                Log.w("TestMapFrag", "snapshot Error", e)
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
                    captionText = marker.title!!
                    setOnClickListener {
                        Toast.makeText(requireContext(), "마커 클릭됨", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("studyId", marker.studyId)
                        startActivity(intent)
                        false
                    }
                }
            }
        }

        // viewModel에 저장된 위치로 카메라 이동
        val latLng = LatLng(viewModel.latitude, viewModel.longitude)
        val cameraUpdate = CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

        // 현위치 똥글이 지도 표시
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow


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
        Log.d("지도", "onResume")

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                Log.d("지도", "추적 꺼짐")
                naverMap!!.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}