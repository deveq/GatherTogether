package com.soldemom.navermapactivity

class Chat(val time: Long, val text: String, val uid: String) {
    constructor() : this(0,"","")
    var type: Int = 0
}