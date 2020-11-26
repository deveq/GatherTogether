package com.soldemom.navermapactivity

data class User(var uid: String) {
    var name: String? = null
    var studyList: List<String> = listOf<String>()
    var introduce: String = ""
    var image: String? = null

    constructor() : this("")
}