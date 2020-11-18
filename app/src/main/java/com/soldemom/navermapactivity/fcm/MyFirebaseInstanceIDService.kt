package com.soldemom.navermapactivity.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    val TAG = "MyFirebaseIIDService"

    override fun onTokenRefresh() {
        //fcm토큰을 가지고 있는 휴대폰은 이 프로젝트에 한해 알림을 받을 수 있음

        val token = FirebaseInstanceId.getInstance().getToken()
        Log.d(TAG,"token은 $token")

        sendRegistrationToServer(token!!)


    }

    fun sendRegistrationToServer(token: String) {
        //앱서버로 토큰을 보낼때 활용


    }
}