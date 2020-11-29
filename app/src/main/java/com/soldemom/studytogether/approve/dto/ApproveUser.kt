package com.soldemom.studytogether.approve.dto

class ApproveUser(val name:String, var introduce: String, val uid: String) {
    constructor() : this("","","")
}