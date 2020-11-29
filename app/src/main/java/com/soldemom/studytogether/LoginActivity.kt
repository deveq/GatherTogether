package com.soldemom.studytogether

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.studytogether.main.dto.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.join_layout.view.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var pwChecked: Boolean = false
    lateinit var googleApiClient: GoogleApiClient
    val REQ_SIGN_GOOGLE = 100
    var deleteFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val toMainintent = Intent(this, MainActivity::class.java)

        val joinView = LayoutInflater.from(this).inflate(R.layout.join_layout, null)

        auth = Firebase.auth

        db = FirebaseFirestore.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //만약 fragment로 구현할 경우 Builder안에 this 대신 getContext() 사용
        googleApiClient = GoogleApiClient.Builder(this).
            enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        //구글 로그인을 눌렀을때
        google_login.setOnClickListener {
            //구글 로그인 창으로 넘어가는 intent를 받아온 후
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)

            //startActivityForResult에 넣어줌.
            startActivityForResult(signInIntent,REQ_SIGN_GOOGLE)

        }




        if (auth.currentUser != null) {
            startActivity(toMainintent)
            finish()
        }

        login_btn.setOnClickListener {
            val email = email_input.text.toString()
            val pw = pw_input.text.toString()
            auth.signInWithEmailAndPassword(email, pw)
                .addOnSuccessListener {
                    startActivity(toMainintent)
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
                                startActivity(toMainintent)
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

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    //구글 로그인 인증을 했을때 결과값을 되돌려 받는곳
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_SIGN_GOOGLE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                //result.signInAccount에는 구글로그인 정보를 담고있음. (닉네임, 프사url, 메일주소 등)
                val account = result.signInAccount
                resultLogin(account!!)
            }

        }
    }

    fun resultLogin(account: GoogleSignInAccount) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) { //로그인 성공했으면
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                    val user = User(auth.currentUser?.uid ?: "왜 없음?")
                    user.name = account.displayName

                    db.collection("users").document(user.uid)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "가입성공", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                        }

                } else { //로그인 실패했으면
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                }
            }

    }
}