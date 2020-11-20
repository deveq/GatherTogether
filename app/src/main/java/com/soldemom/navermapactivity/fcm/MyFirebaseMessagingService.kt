package com.soldemom.navermapactivity.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.soldemom.navermapactivity.LoginActivity
import com.soldemom.navermapactivity.MainActivity
import com.soldemom.navermapactivity.R

//class MyFirebaseMessagingService : FirebaseMessagingService(){
////
////
////    val Tag = "FirebaseMsgService"
////
////    lateinit var msg: String
////    lateinit var title: String
////
////
////    override fun onMessageReceived(remoteMessage: RemoteMessage) {
////        Log.d(Tag, "onMessageReceived : $remoteMessage")
////
////        title = remoteMessage.notification!!.title!!
////        msg = remoteMessage.notification!!.body!!
////
////        val intent = Intent(this, MainActivity::class.java)
////
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////
////        val contentIntent = PendingIntent.getActivity(
////            this, 0, Intent(
////                this,
////                MainActivity::class.java
////            ), 0
////        )
////
////        val mBuilder = NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
////            .setContentTitle(title)
////            .setContentText(msg)
////            .setAutoCancel(true)
////            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
////            .setVibrate(longArrayOf(1,1000))
////
////        val notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager
////
////        notificationManager.notify(0, mBuilder.build())
////
////        mBuilder.setContentIntent(contentIntent)
////
////
////
////
////    }
////}

//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//
//
//    val tag = "FCM Log"
//
//    //토큰이 변경되었을 때
//    override fun onNewToken(token: String) {
//        Log.d(tag, "Refreshed token : $token")
//
//        //변경된 토큰을 Firestore의 user내에 있는 fcmToken에 넣어줘야함.
//        val db = FirebaseFirestore.getInstance()
//        val auth = Firebase.auth
//        if (auth.currentUser != null) {
//            db.collection("users")
//                .document(auth.currentUser!!.uid)
//                .update("fcmToken",token)
//        }
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//
//        if (remoteMessage.notification != null) {
//            Log.d(tag, "알림 메시지 : ${remoteMessage.notification!!.body}")
//            val messageBody = remoteMessage.notification!!.body
//            val messageTitle = remoteMessage.notification!!.title
//            val intent = Intent(this, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            //부산역 출발 서울역 도착 intent를 지금 당장 실행시키는것이 아니라
//            //서울역 도착하는 intent를 미리 만들어놨다가 특정 조건에 누군가가 서울역으로 가라고 시키는 intent
//            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//            val channelId = "Channel Id"
//            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val notificationBuilder =
//                NotificationCompat.Builder(this,channelId)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(messageTitle)
//                    .setContentText(messageBody)
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setContentIntent(pendingIntent)
//            val notificationManager = (getSystemService(NOTIFICATION_SERVICE)) as NotificationManager
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channelName = "Channel Name"
//                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
//                notificationManager.createNotificationChannel(channel)
//
//            }
//            notificationManager.notify(0, notificationBuilder.build())
//        }
//    }
//}