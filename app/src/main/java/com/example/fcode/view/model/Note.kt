package com.example.fcode.view.model

import java.util.Date

data class Note(
    val creationDate: String,
    val title: String,
    val content: String,
    val userName: String
){
    constructor(): this("","","","")
}
