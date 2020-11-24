package com.soldemom.navermapactivity.kakaoLocal

import com.google.gson.JsonObject
import com.soldemom.navermapactivity.testFrag.DocAddr
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitService{

    @GET("/v2/local/search/address.json")
    fun getByAdd(
        @Header("Authorization") appKey: String,
        @Query("query") address: String
    ) : Call<DocAddr>

    @GET("/v2/local/geo/coord2regioncode.json")
    fun getByGeo(
        @Header("Authorization") appKey: String,
        @Query("x") x: Double,
        @Query("y") y: Double
    ) : Call<DocAddr>
}