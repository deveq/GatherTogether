package com.soldemom.navermapactivity

data class User(var uid: String) {
    var name: String? = null
    var studyList: List<String>? = null

    constructor() : this("")
}