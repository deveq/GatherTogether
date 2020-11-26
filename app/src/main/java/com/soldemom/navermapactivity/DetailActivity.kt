package com.soldemom.navermapactivity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.soldemom.navermapactivity.testFrag.DetailAdapter
import com.soldemom.navermapactivity.testFrag.DetailChatFragment
import com.soldemom.navermapactivity.testFrag.DetailFragAdapter
import com.soldemom.navermapactivity.testFrag.StudyInfoFragment
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_study_info.view.*
import java.io.ByteArrayOutputStream

class DetailActivity : AppCompatActivity() {

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var point: Point
    lateinit var uid: String
    lateinit var studyId: String
    lateinit var list: MutableList<Fragment>
    lateinit var storage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        studyId = intent.getStringExtra("studyId")

        list = mutableListOf<Fragment>(
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == StudyInfoFragment.REQ_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val image: Uri = data!!.data!!
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image)
                (list[0] as StudyInfoFragment).fragView.detail_profile_image.setImageBitmap(bitmap)
                storage = Firebase.storage
                val studyImageRef =
                    storage.reference.child("studyImage/${studyId}.jpg")
                studyId

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos)
                val imageData = baos.toByteArray()

                var uploadTask = studyImageRef.putBytes(imageData)
                val urlTask = uploadTask.continueWithTask {task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    studyImageRef.downloadUrl
                }.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Log.d("임둥","${downloadUri.toString()}")
                        db.collection("markers").document(studyId)
                            .update("image",downloadUri.toString())
                    }
                }
            }
        }
    }
}
