package com.soldemom.navermapactivity

import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng

data class Point(var geoPoint: GeoPoint? = null, var title: String? = null) {

    constructor() : this(null, null)

    var maxCount: Long? = null
    var currentCount: Long? = null
    var leader: String? = null //uid넣기
    var member: MutableList<String>? = null //uid 넣기
    var date: Long? = null
    var tag: String? = null
    var text: String? = null
}