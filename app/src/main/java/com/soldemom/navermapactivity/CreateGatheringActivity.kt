package com.soldemom.navermapactivity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_gathering.*

class CreateGatheringActivity : AppCompatActivity(), View.OnClickListener {


    val auth: FirebaseAuth = Firebase.auth
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var spinnerResult: Int = 2
    lateinit var name: String
    val latitude: Double by lazy {
        intent.getDoubleExtra("latitude",0.0)
    }
    val longitude: Double by lazy {
        intent.getDoubleExtra("longitude",0.0)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_gathering)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerResult = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }




        name_input
        text_input
        create_btn.setOnClickListener(this)
        cancel_btn.setOnClickListener(this)





    }

    override fun onClick(v: View?) {
        if (v == create_btn) {
            val geoPoint = GeoPoint(latitude,longitude)
            name = name_input.text.toString()
            val uid = auth.currentUser?.uid
            val detailText = text_input.text.toString()
            val point = Point(geoPoint,name).apply {
                maxCount = spinnerResult.toLong()
                leader = uid
                currentCount = 1
                member = mutableListOf<String>(uid!!)
                text = detailText

            }
            db.collection("markers").add(point)
                .addOnSuccessListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"실패했따는데..",Toast.LENGTH_SHORT).show()
                }
        } else if (v == cancel_btn) {
            setResult(Activity.RESULT_CANCELED)
            finish()

        }
    }
}