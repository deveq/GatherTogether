package com.soldemom.navermapactivity.testFrag

import android.util.Log

data class DocAddr(val documents: List<AddressObject>)

data class AddressObject(
    var address_name: String,
    val x: Double,
    val y: Double,


) {
//    lateinit var depth1: String
//    lateinit var depth2: String
//    lateinit var depth3: String
    fun changeDepth1() {
        val add = address_name.split(" ").toMutableList()
        add[0] = addrMap[add[0]]!!
        val tempAddr = listOf<String>(add[0], add[1])
        address_name = tempAddr.joinToString(" ")
    }

    companion object {
        val addrMap = hashMapOf<String, String>(
            "서울" to "서울특별시",
            "부산" to "부산광역시",
            "대구" to "대구광역시",
            "인천" to "인천광역시",
            "광주" to "광주광역시",
            "대전" to "대전광역시",
            "울산" to "울산광역시",
            "강원" to "강원도",
            "경기" to "경기도",
            "경남" to "경상남도",
            "경북" to "경상북도",
            "전남" to "전라남도",
            "전북" to "전라북도",
            "충남" to "충청남도",
            "충북" to "충청북도",
        )
    }
}

