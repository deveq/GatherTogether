package com.soldemom.navermapactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_google_login_result.*

class GoogleLoginResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login_result)

        tv_result
        iv_profile

        val nickname = intent.getStringExtra("nickname")
        val photoUrl = intent.getStringExtra("photoUrl")

        tv_result.text = nickname

        Glide.with(this).load(photoUrl).into(iv_profile)

    }
}