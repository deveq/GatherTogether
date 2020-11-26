package com.soldemom.navermapactivity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.soldemom.navermapactivity.fragment.MapFrag
import com.soldemom.navermapactivity.testFrag.*
import kotlinx.android.synthetic.main.activity_test3.*
import kotlinx.android.synthetic.main.fragment_study_info.view.*
import kotlinx.android.synthetic.main.fragment_test_info.*
import java.io.ByteArrayOutputStream

class TestActivity3 : AppCompatActivity() {

    val tag = "FCM"
    private lateinit var locationSource: FusedLocationSource

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var markerList: MutableList<Point>
    val mapFrag = TestMapFrag()
    lateinit var fragList: MutableList<Fragment>
    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test3)


        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mapFrag.locationSource = locationSource

        getMarkers()

    }

    private fun getMarkers() {
        db.collection("markers")
            .get()
            .addOnSuccessListener { querySnapshot ->
                markerList = querySnapshot.toObjects(Point::class.java)
                mapFrag.markerList = markerList

                //

                fragList = mutableListOf<Fragment>(
                    mapFrag,
                    SearchFragment(),
                    TestAttendFrag(),
                    TestInfoFrag()
                )

                testViewPager.adapter = TestFragAdapter(this).apply {
                    list = fragList
                }

                testViewPager.isUserInputEnabled = false
                
                val nameList = listOf<String>("지도찾기","리스트찾기","가입목록","정보")

                TabLayoutMediator(testTab, testViewPager) { tab, position ->
                    tab.text = nameList[position]
                }.attach()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                val image: Uri = data!!.data!!
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image)
                (fragList[3] as TestInfoFrag).info_profile_image.setImageBitmap(bitmap)
                storage = Firebase.storage
                val uid = auth.currentUser!!.uid
                val userImageRef =
                    storage.reference.child("userImage/${uid}.jpg")

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos)
                val imageData = baos.toByteArray()

                var uploadTask = userImageRef.putBytes(imageData)
                val urlTask = uploadTask.continueWithTask {task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    userImageRef.downloadUrl
                }.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        db.collection("users").document(uid)
                            .update("image",downloadUri.toString())
                    }
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val REQ_IMAGE_CODE = 1002
    }
}