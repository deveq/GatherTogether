package com.soldemom.navermapactivity

class Chat(val time: Long, val text: String, val uid: String) {
    constructor() : this(0,"","")
    var type: Int = 0

    companion object {
        const val CHAT_TYPE = 0
        const val DATE_TYPE = 1
    }

}