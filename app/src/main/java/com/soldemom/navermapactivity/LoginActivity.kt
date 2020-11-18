package com.soldemom.navermapactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val intent = Intent(this, TestActivity3::class.java)

        auth = Firebase.auth

        db = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {
            startActivity(intent)
            finish()
        }

        login_btn.setOnClickListener {
            val email = email_input.text.toString()
            val pw = pw_input.text.toString()
            auth.signInWithEmailAndPassword(email,pw)
                .addOnSuccessListener {
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this).setTitle("로그인실패").setPositiveButton("확인",null).create().show()
                }
        }
        join_btn.setOnClickListener {
            val email = email_input.text.toString()
            val pw = pw_input.text.toString()
            auth.createUserWithEmailAndPassword(email,pw)
                .addOnSuccessListener {
                    val user = User(it.user!!.uid)
                    user.name = name_input.text.toString()
                    user.studyList = listOf<String>()
                    db.collection("users").document(user.uid)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this,"가입성공",Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                        }
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this).setTitle("가입실패").setPositiveButton("확인",null).create().show()
                }


        }




    }
}