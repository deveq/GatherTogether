package com.soldemom.navermapactivity

import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng

data class Point(var geoPoint: GeoPoint? = null, var title: String? = null) {

    constructor() : this(null, null)

    //생성일
    var createdTime: Long? = null
    // 리더 uid
    var leader: String? = null //uid넣기
    // 멤버 uid의 list
    var member: MutableList<String>? = null //uid 넣기
    // 최대 멤버 수
    var maxCount: Long? = null
    // 현재 인원
    var currentCount: Long? = null
    var date: Long? = null
    var tag: String? = null
    // 스터디 설명
    var text: String? = null
    var studyId: String? = null
    //가입 대기목록
    val waitingMember= mapOf<String, String>()
}