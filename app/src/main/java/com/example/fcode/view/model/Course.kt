package com.example.fcode.view.model

data class Course(
    val nameCourse: String,
    val author : String,
    val document: String,
    val noteCourse: String,
    val img: String
){
    constructor():this("","","","","")
}
