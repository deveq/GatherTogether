package com.soldemom.navermapactivity.fcm.retrofit

import retrofit2.Call
import retrofit2.http.*

interface NotiService {
//    @POST("send")
//    @FormUrlEncoded
//    fun getget(
//        @HeaderMap headers: Map<String, String>,
//        @Field("to") token: String,
//        @Field("notification") noti: Noti
//    ) : Call<Any>

    @POST("send")
    fun sendsend(
        @Body noti: Noti) : Call<Void>


}