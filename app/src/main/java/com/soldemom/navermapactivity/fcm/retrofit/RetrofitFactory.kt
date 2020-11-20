package com.soldemom.navermapactivity.fcm.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {


    companion object {
    private val url = "https://fcm.googleapis.com/fcm/"
        fun create() : NotiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(NotiService::class.java)
        }
    }


}