package com.soldemom.navermapactivity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.join_layout.view.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var pwChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val intent = Intent(this, TestActivity3::class.java)

        val joinView = LayoutInflater.from(this).inflate(R.layout.join_layout, null)

        auth = Firebase.auth

        db = FirebaseFirestore.getInstance()


        if (auth.currentUser != null) {
            startActivity(intent)
            finish()
        }

        login_btn.setOnClickListener {
            val email = email_input.text.toString()
            val pw = pw_input.text.toString()
            auth.signInWithEmailAndPassword(email, pw)
                .addOnSuccessListener {
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this).setTitle("로그인실패").setPositiveButton("확인", null)
                        .create().show()
                }
        }
        join_btn.setOnClickListener {

            if (joinView.parent != null) {
                (joinView.parent as ViewGroup).removeView(joinView)
            }

            val dialog = AlertDialog.Builder(this)
                .setView(joinView)
                .create()

            joinView.join_close.setOnClickListener {
                dialog.cancel()
            }

            joinView.join_button.setOnClickListener {
                val email = joinView.join_id_input.text.toString()
                val password = joinView.join_password.text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val user = User(it.user!!.uid)
                        user.name = joinView.join_name_input.text.toString()

                        db.collection("users").document(user.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "가입성공", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
                            }
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(this)
                            .setTitle("가입실패")
                            .setMessage(it.message)
                            .setPositiveButton("확인", null)
                            .create().show()
                    }


            }

            joinView.join_password_confirm.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val password1 = joinView.join_password.text.toString()
                    joinView.join_button.isClickable = (password1 == s.toString())
                }
            })

            dialog.show()

        }


    }
}