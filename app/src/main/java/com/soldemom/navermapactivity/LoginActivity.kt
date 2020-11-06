package com.soldemom.navermapactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val intent = Intent(this, MainActivity::class.java)

        auth = Firebase.auth

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
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this).setTitle("가입실패").setPositiveButton("확인",null).create().show()
                }


        }




    }
}