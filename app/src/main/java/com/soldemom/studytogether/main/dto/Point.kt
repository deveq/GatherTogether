package com.soldemom.studytogether.main.dto

import com.google.firebase.firestore.GeoPoint

data class Point(var geoPoint: GeoPoint? = null, var title: String? = null) {

    constructor() : this(null, null)

    // 리더 uid
    var leader: String? = null //uid넣기
    // 멤버 uid의 list
    var member: MutableList<String>? = null //uid 넣기
    // 최대 멤버 수
    var maxCount: Long? = null
    // 현재 인원
    var currentCount: Long? = null
    // 스터디 설명
    var text: String? = null
    // 스터디의 고유 ID
    var studyId: String? = null
    //가입 대기목록
    val waitingMember= mapOf<String, String>()
    //주소
    var address: String? = null
    //스터디 이미지
    var image: String? = null
}